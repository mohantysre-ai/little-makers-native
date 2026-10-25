from pathlib import Path
import xml.etree.ElementTree as E
r=Path(__file__).resolve().parents[1];j=r/'app/src/main/java/com/littlemakers/village/nativeapp';a='{http://schemas.android.com/apk/res/android}'
m=E.parse(r/'app/src/main/AndroidManifest.xml').getroot();app=m.find('application');activities={v.get(a+'name'):v for v in app.findall('activity')};assert activities['com.littlemakers.village.nativeapp.AdventureActivity'].get(a+'exported')=='false'
assert 'new Intent(this,AdventureActivity.class)' in (j/'OpeningActivity.java').read_text()
assert 'getStringExtra("destination")' in (j/'CreativeActivity.java').read_text() and 'adventure_return' in (j/'MainActivity.java').read_text()
assert 'getSharedPreferences("adventure",0).edit().clear().apply()' in (j/'MainActivity.java').read_text()
wallet=(j/'PlayerWallet.java').read_text();assert '(story|texture|dots|shape|adventure|guardian|balance|robot|discovery|letters|island|friends|tangram|maker)-[0-9]+' in wallet and 'CONFLICT_IGNORE' in wallet
assert not m.findall('uses-permission')
print('PASS: internal adventure entry, existing learning navigation, parent reset integration, shared idempotent wallet and no new permissions')
