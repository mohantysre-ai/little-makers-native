"""Inventory checks: semantic coverage, not a claim of GPU/device visual acceptance."""
from pathlib import Path
import json,re
r=Path(__file__).resolve().parents[1];j=r/'app/src/main/java/com/littlemakers/village/nativeapp'
stories=json.loads((r/'app/src/main/assets/webart/stories.json').read_text())
props={s['item'] for ep in stories for s in ep['steps']}
models=(j/'StoryModels.java').read_text();codes=set(re.findall(r'"([0-9A-F]{4,6})"',models));mapped={f'pictures/color-svg/{c}.svg' for c in codes}|set(re.findall(r'm.put\("([^"]+)"',models));assert props<=mapped,props-mapped
renderer=(j/'NativeStageSurface.java').read_text();declarations=re.findall(r'([A-Z]+)=\d+',models.split('static final Map')[0]);assert all(f'case StoryModels.{x}:' in renderer for x in declarations)
view=(j/'GameView.java').read_text();choices=next(line for line in view.splitlines() if 'private void shapeButton(' in line);assert 'Artwork.shape(' not in choices and 'shape(c,' in choices
assert 'stage.submit(new SceneFrame' in view and 'sceneNodes.add' in view
assert 'gameView.stage(stage)' in (j/'MainActivity.java').read_text()
creative=(j/'CreativeActivity.java').read_text();assert 'nativeStage.submit(storyFrame())' in creative and 'StoryModels.forItem' in creative and 'nativeStage.image(preview)' in creative
assert 'setRenderMode(reduced?RENDERMODE_WHEN_DIRTY' in renderer and 'time-winAt<1.6f' in renderer
print(f'PASS: all {len(stories)} scripts / {sum(len(ep["steps"]) for ep in stories)} steps have explicit 3D model coverage; {len(props)} prop assets; native game/studio wiring. Device rendering tested separately.')
