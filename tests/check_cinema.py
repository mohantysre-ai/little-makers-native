"""Validate real bundled media; fail closed on missing, changed, silent-black or mislabeled resources."""
from pathlib import Path
import json,hashlib,subprocess,re,struct
r=Path(__file__).resolve().parents[1];j=r/'app/src/main/java/com/littlemakers/village/nativeapp'
m=json.loads((r/'app/src/main/assets/cinema/media_manifest.json').read_text())
assert m['film_count']==1 and m['distinct_clips']==len(m['clips'])==4
assert 'not live camera' in m['source']['media_type']
clips=set();checks=0
for c in m['clips']:
 p=r/'app/src/main'/c['resource'];poster=r/'app/src/main'/c['poster'];assert p.exists() and poster.exists();data=p.read_bytes();assert hashlib.sha256(data).hexdigest()==c['sha256'];assert len(data)==c['bytes'];checks+=4
 info=json.loads(subprocess.check_output(['ffprobe','-v','error','-show_streams','-show_format','-of','json',str(p)]))
 streams=info['streams'];assert len(streams)==1 and streams[0]['codec_type']=='video' and streams[0]['codec_name']=='h264';v=streams[0];assert v['profile']=='Constrained Baseline' and v['pix_fmt']=='yuv420p' and v['width']==960 and v['height']==540;assert 7.9<float(info['format']['duration'])<8.2;checks+=3
 atom=[];pos=0
 while pos+8<=len(data):
  size,kind=struct.unpack('>I4s',data[pos:pos+8]);assert size>=8;atom.append(kind);pos+=size
 assert b'moov' in atom and b'mdat' in atom and atom.index(b'moov')<atom.index(b'mdat');checks+=1
 frames=subprocess.check_output(['ffmpeg','-v','error','-i',str(p),'-vf','fps=2,scale=64:36','-f','framemd5','-']).decode();hashes=[line.split(',')[-1].strip() for line in frames.splitlines() if not line.startswith('#')];assert len(set(hashes))>=12,'video is static';checks+=1;clips.add(c['id'])
from PIL import Image
atlas=r/'app/src/main/assets/materials/physical_atlas.webp';im=Image.open(atlas);assert im.size==(1024,1024);assert len(set(im.resize((64,64)).get_flattened_data()))>100
source=(j/'CinematicState.java').read_text();assert set(re.findall(r'new Scene\("([a-z_]+)"',source))==clips
activity=(j/'CinemaActivity.java').read_text();assert 'android.resource://' in activity and 'setOnCompletionListener' in activity and 'setOnErrorListener' in activity and 'onPause' in activity
assert 'cinema-0' in activity and 'mp.setVolume(0,0)' in activity and 'pendingStart=false' in activity
for name in ['NativeStageSurface','VillageSurface','AdventureSurface','PlaySceneSurface','HabitatSurface','ClaySurface']:
 s=(j/(name+'.java')).read_text();assert 'MaterialTexture' in s and 'materials.bind()' in s,name
assert 'getSharedPreferences("cinema",0).edit().clear().apply()' in (j/'MainActivity.java').read_text()
print(f'PASS {checks} real MP4 metadata/hash/fast-start/moving-frame checks, 4 distinct offline clips, 1 branching film, material atlas on all 6 renderers, local navigation and parent-reset wiring. Not a photorealism or Play-policy certificate.')

resources=(j/"CinematicResources.java").read_text()
for clip in clips:assert "R.raw."+clip in resources and "R.drawable."+clip in resources
assert "getIdentifier(" not in activity
print("PASS explicit media resource allowlist independent of runtime application ID.")
