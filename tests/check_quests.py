from pathlib import Path
j=Path(__file__).resolve().parents[1]/'app/src/main/java/com/littlemakers/village/nativeapp'
s=(j/'QuestActivity.java').read_text()
root=Path(__file__).resolve().parents[1]
assert 'QuestActivity" android:exported="false"' in (root/'app/src/main/AndroidManifest.xml').read_text()
for name in ['AdventureActivity','MainActivity']:assert 'new Intent(this,QuestActivity.class)' in (j/(name+'.java')).read_text()
for name in ['NativeStageSurface','creativeCompletion','onPause','onResume','onSaveInstanceState','reduce_motion','getLaunchIntentForPackage']:assert (name in s)==(name!='getLaunchIntentForPackage'),name
assert 'learning_quests' in (j/'MainActivity.java').read_text() and 'quest_reset_generation' in s
assert 'isNetworkConnectionRequired()' in s
assert not any(x in s for x in ['WebView','market://','http://','https://'])
assert 'CONFLICT_IGNORE' in (j/'PlayerWallet.java').read_text()
print('PASS: internal quest entry, native mesh/lifecycle/theme/rewards, parent reset and offline speech guards')
