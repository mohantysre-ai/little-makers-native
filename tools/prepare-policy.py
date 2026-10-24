#!/usr/bin/env python3
"""One policy source generates matching offline and HTML copies. Release refuses missing identity."""
from pathlib import Path
import argparse,json,re,html
from urllib.parse import urlparse
root=Path(__file__).resolve().parents[1]
p=argparse.ArgumentParser();p.add_argument('--release',action='store_true');args=p.parse_args()
config=json.loads((root/'release/publisher-config.json').read_text())
errors=[]
for key in ['publisher_legal_name','support_email','public_privacy_url']:
 if not config.get(key,'').strip():errors.append(key+' is missing')
if config.get('support_email') and not re.fullmatch(r'[^\s@]+@[^\s@]+\.[^\s@]+',config['support_email']):errors.append('Invalid support email')
if config.get('public_privacy_url'):
 parsed=urlparse(config['public_privacy_url'])
 if parsed.scheme!='https' or not parsed.hostname or any(c.isspace() for c in config['public_privacy_url']) or parsed.username:errors.append('Policy URL must be a valid public HTTPS address')
 if parsed.hostname in ('example.com','example.org','localhost'):errors.append('Placeholder/private policy host is not allowed')
if args.release and errors:raise SystemExit('Release policy blocked: '+', '.join(errors))
text=(root/'release/policies/privacy-template.txt').read_text()
for key,value in config.items():text=text.replace('{{'+key+'}}',str(value).strip() or '[NOT CONFIGURED - DEVELOPMENT BUILD]')
if re.search(r'{{.*?}}',text):raise SystemExit('Unresolved template field')
if errors:text='DEVELOPMENT BUILD - PUBLISHER DETAILS PENDING\nDo not submit this build to Google Play.\n\n'+text
(root/'app/src/main/res/raw/privacy.txt').write_text(text)
(root/'release/policies/privacy.txt').write_text(text)
page='<!doctype html><html lang="en"><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Privacy policy - Little Makers</title><style>body{max-width:820px;margin:40px auto;padding:20px;font:18px/1.6 system-ui;background:#f6f4ff;color:#202941}pre{white-space:pre-wrap;font:inherit}</style><main><pre>'+html.escape(text)+'</pre></main></html>'
(root/'release/policies/privacy.html').write_text(page)
print('Policy copies generated.'+(' DEVELOPMENT ONLY: '+', '.join(errors) if errors else ' Publisher fields configured. Public accessibility still needs verification.'))
