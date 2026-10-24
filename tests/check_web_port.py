"""Regression: no missing catalog thumbnails, line art, episode props or branch endings."""
from pathlib import Path
import json,struct
r=Path(__file__).resolve().parents[1]/'app/src/main/assets/webart'
pictures=json.loads((r/'catalog.json').read_text());episodes=json.loads((r/'stories.json').read_text())
assert len(pictures)==1053 and len(episodes)==210
paths={p['colorPath'] for p in pictures}
checks=0
for p in pictures:
 for key in ['nativeLine','nativeColor']:
  f=r.parent/p[key];data=f.read_bytes();assert data[:8]==b'\x89PNG\r\n\x1a\n',str(f)
  w,h=struct.unpack('>II',data[16:24]);assert w>0 and h>0;checks+=1
for ep in episodes:
 assert ep['id'] and ep['title'] and ep['steps'];assert len(ep['choices'])==len(ep['endings'])>=2
 for step in ep['steps']:
  item=step['item'];assert step['label'] and step['line'] and step['result'];assert 0<=step['x']<=100 and 0<=step['y']<=100
  if item.endswith('.png'):assert (r/'story'/item.split('/')[-1]).is_file(),item
  else:assert any(item.endswith(p) for p in paths),item
  checks+=1
assert (r/'LICENSE.txt').is_file() and (r/'ATTRIBUTION.md').is_file()
print(f'PASS: {len(pictures)} picture entries, {len(episodes)} web episodes; {checks} asset/step checks; every story prop and branch has content.')
