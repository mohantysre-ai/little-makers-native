from pathlib import Path
import json,subprocess,sys,xml.etree.ElementTree as ET,html,re
root=Path(__file__).resolve().parents[1]
listing=json.loads((root/'release/listing-en-IN.json').read_text())
assert 0<len(listing['title'])<=30
assert 0<len(listing['short_description'])<=80
assert 0<len(listing['full_description'])<=4000
for unavailable in ['export','narration','break reminder']:
 assert unavailable.lower() not in str(listing).lower(),unavailable
assert '1,053' in listing['full_description'] and '210' in listing['full_description']
assert '1,200 ACTIVITY LEVELS' in listing['full_description'] and '20 game types' in listing['full_description']
assert (root/'app/src/main/res/raw/privacy.txt').read_text()==(root/'release/policies/privacy.txt').read_text()
page=(root/'release/policies/privacy.html').read_text()
assert html.escape((root/'release/policies/privacy.txt').read_text()) in page
java=(root/'app/src/main/java/com/littlemakers/village/nativeapp/MainActivity.java').read_text()
assert 'Api33Back.register(this)' in java and 'Build.VERSION.SDK_INT>=33' in java
assert 'registerOnBackInvokedCallback' in java and 'a::navigateBack' in java
assert 'NavigationPolicy.back(screen,gameId)' in java
assert 'addButton("Privacy policy",()->privacy())' in java
assert 'setTextIsSelectable(true)' in java
canvas=(root/'app/src/main/java/com/littlemakers/village/nativeapp/GameView.java').read_text()
assert 'n.setSource(GameView.this)' in canvas and 'Rect.intersects(bounds,visible)' in canvas
assert 'GesturePolicy.capture' in canvas
assert '"RESET".equals' in java and 'screen.equals("parent")' in java
manifest=ET.parse(root/'app/src/main/AndroidManifest.xml').getroot();ns='{http://schemas.android.com/apk/res/android}'
app=manifest.find('application');assert app.attrib[ns+'allowBackup']=='false'
assert ns+'dataExtractionRules' in app.attrib and app.attrib[ns+'enableOnBackInvokedCallback']=='true'
rules=ET.parse(root/'app/src/main/res/xml/data_extraction_rules.xml').getroot()
for mode in ['cloud-backup','device-transfer']:
 assert {x.attrib['domain'] for x in rules.find(mode)}=={'root','file','database','sharedpref','external'}
config=json.loads((root/'release/publisher-config.json').read_text())
if not all(config[k] for k in ['publisher_legal_name','support_email','public_privacy_url']):
 result=subprocess.run([sys.executable,str(root/'tools/prepare-policy.py'),'--release'],capture_output=True,text=True)
 assert result.returncode!=0 and 'Release policy blocked' in result.stderr
signing=(root/'app/build.gradle').read_text();assert 'verifyReleaseSigning' in signing and "'preReleaseBuild'" in signing
assert 'System.getenv' in signing and 'signingConfigs.debug' not in signing
print('PASS: native listing accuracy/limits, matching offline/HTML policy, predictive Back wiring, protected reset, backup exclusions and missing-identity/signing release guards.')
