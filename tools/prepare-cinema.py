#!/usr/bin/env python3
"""Reproduce the reviewed bundled excerpts from an already-downloaded, matching source file."""
from pathlib import Path
import argparse,hashlib,json,subprocess
parser=argparse.ArgumentParser();parser.add_argument('source',type=Path);args=parser.parse_args()
root=Path(__file__).resolve().parents[1];manifest=root/'app/src/main/assets/cinema/media_manifest.json';m=json.loads(manifest.read_text())
if hashlib.sha256(args.source.read_bytes()).hexdigest()!=m['source']['sha256']:raise SystemExit('Source checksum differs from reviewed media; stop and review its origin/rights before changing the manifest.')
for clip in m['clips']:
 name=clip['id']
 if not name.startswith('moon_') or not name.replace('_','').isalpha():raise SystemExit('Invalid local resource name')
 out=root/'app/src/main/res/raw'/f'{name}.mp4';poster=root/'app/src/main/res/drawable-nodpi'/f'{name}.webp'
 subprocess.run(['ffmpeg','-hide_banner','-loglevel','error','-ss',str(clip['source_start_seconds']),'-i',str(args.source),'-t',str(clip['duration_seconds']),'-an','-vf','scale=960:540:flags=lanczos','-r','24','-c:v','libx264','-profile:v','baseline','-level:v','3.1','-pix_fmt','yuv420p','-crf','23','-preset','fast','-movflags','+faststart','-y',str(out)],check=True)
 subprocess.run(['ffmpeg','-loglevel','error','-ss','1','-i',str(out),'-frames:v','1','-vf','scale=640:360','-y',str(poster)],check=True)
 clip['sha256']=hashlib.sha256(out.read_bytes()).hexdigest();clip['bytes']=out.stat().st_size
manifest.write_text(json.dumps(m,indent=2)+'\n')
print('Prepared four reviewed clips. Run tools/test.sh and device cinema checks before publishing.')
