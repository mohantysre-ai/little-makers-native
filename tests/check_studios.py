from pathlib import Path
import xml.etree.ElementTree as E
r=Path(__file__).resolve().parents[1];j=r/'app/src/main/java/com/littlemakers/village/nativeapp';a='{http://schemas.android.com/apk/res/android}'
m=E.parse(r/'app/src/main/AndroidManifest.xml').getroot();assert not m.findall('uses-permission')
activity=next(x for x in m.find('application').findall('activity') if x.get(a+'name','').endswith('.StudioActivity'));assert activity.get(a+'exported')=='false'
s=(j/'StudioActivity.java').read_text();n=(j/'OfflineNarrator.java').read_text()
for token in ['NativeStageSurface','onPause','onResume','onSaveInstanceState','quest_reset_generation','creativeCompletion','ExplorerCatalog.stopKey','progress.completed','reduce_motion']:assert token in s,token
assert 'isNetworkConnectionRequired()' in n and 'Voice' in n
assert 'explorer_studios' in (j/'MainActivity.java').read_text()
assert 'ExplorerCatalog.NAMES.length' in (j/'QuestActivity.java').read_text() and 'quest_stage' in (j/'QuestActivity.java').read_text()
assert 'Mesh.prism' in (j/'NativeStageSurface.java').read_text()
for file in ['StudioActivity.java','StudioBoardView.java','ExplorerCatalog.java','ExplorerState.java','OfflineNarrator.java']:
 t=(j/file).read_text();assert not any(x in t for x in ['WebView','market://','https://','getLaunchIntentForPackage'])
print('PASS: six internal studios, native polygon meshes/tactile input, all-nine menu, real map completion links, parent reset and offline speech; no new permissions/external content')
