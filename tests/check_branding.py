from pathlib import Path
import xml.etree.ElementTree as E
r=Path(__file__).resolve().parents[1];a='{http://schemas.android.com/apk/res/android}'
manifest=E.parse(r/'app/src/main/AndroidManifest.xml').getroot();app=manifest.find('application');assert app.get(a+'icon')=='@mipmap/app_icon' and app.get(a+'roundIcon')=='@mipmap/app_icon_round'
activities={x.get(a+'name'):x for x in app.findall('activity')};assert activities['com.littlemakers.village.nativeapp.MainActivity'].get(a+'exported')=='false';opening=activities['com.littlemakers.village.nativeapp.OpeningActivity'];assert opening.get(a+'theme')=='@style/LaunchTheme' and opening.get(a+'exported')=='true';assert opening.find('intent-filter') is not None
for suffix in ['v26','v33']:
 for name in ['app_icon','app_icon_round']:
  icon=E.parse(r/f'app/src/main/res/mipmap-anydpi-{suffix}/{name}.xml').getroot();assert icon.tag=='adaptive-icon' and icon.find('foreground') is not None and icon.find('background') is not None
  if suffix=='v33':assert icon.find('monochrome') is not None
assert (r/'app/src/main/assets/webart/forest_backdrop.webp').stat().st_size<800000
styles=E.parse(r/'app/src/main/res/values-v31/styles.xml').getroot();assert any(x.get('name')=='android:windowSplashScreenAnimatedIcon' for x in styles.iter('item'))
print('PASS: adaptive/round/themed icons, launch theme, internal opening screen and bounded offline background asset.')
