from pathlib import Path
import sqlite3,tempfile,re,json,math
root=Path(__file__).resolve().parents[1]
checks=0

def check(value,message):
 global checks
 checks+=1
 assert value,message

def lum(h):
 rgb=[int(h[i:i+2],16)/255 for i in (0,2,4)]
 return sum((v/12.92 if v<=.04045 else ((v+.055)/1.055)**2.4)*w for v,w in zip(rgb,[.2126,.7152,.0722]))
contrasts={}
for mode,fg,colors in [('dark','ffffff',['171b2d','262d43','343954','574491','167c6d','aa345e','876000']),('light','202941',['f6f4ff','ffffff','ebe7fc','e4ddfa','bcece1','f7c6d8','f4e09f'])]:
 for bg in colors:
  ratio=(max(lum(fg),lum(bg))+.05)/(min(lum(fg),lum(bg))+.05)
  contrasts[f'{mode}/{bg}']=round(ratio,2);check(ratio>=4.5,f'contrast {mode} {bg}: {ratio}')
with tempfile.TemporaryDirectory() as tmp:
 path=Path(tmp)/'wallet.db';db=sqlite3.connect(path);db.executescript((root/'app/src/main/res/raw/schema.sql').read_text())
 def win(id,coins):
  with db:
   first=db.execute('INSERT OR IGNORE INTO completion VALUES(?,?,?,1)',(id,0,coins)).rowcount
   if first:
    db.execute('UPDATE wallet SET coins=coins+?,stars=stars+1 WHERE id=1',(coins,));db.execute('INSERT INTO ledger VALUES(?,?,1,0)',('win:'+id,coins))
   db.execute('INSERT OR REPLACE INTO progress VALUES(?,?,0)',(id,b'state'))
  return first
 for i in range(1200):check(win(str(i),5+((i%60)//4 if i%60<10 else min(2,(i%60-10)//20))*2)==1,'first completion')
 expected=sum(5+(r//4 if r<10 else min(2,(r-10)//20))*2 for r in range(60))*20
 check(db.execute('SELECT coins,stars FROM wallet').fetchone()==(expected,1200),'all catalog rewards')
 for i in range(1200):check(win(str(i),99)==0,'repeat reward blocked')
 check(db.execute('SELECT coins,stars FROM wallet').fetchone()==(expected,1200),'repeat balance')
 with db:
  db.execute("INSERT INTO inventory VALUES('sky',0)");db.execute('UPDATE wallet SET coins=coins-25 WHERE id=1');db.execute("INSERT INTO ledger VALUES('purchase:sky',-25,0,0)");db.execute("INSERT INTO equipped VALUES('skin','sky')")
 try:
  with db:db.execute('UPDATE wallet SET coins=coins-999999 WHERE id=1')
 except sqlite3.IntegrityError:pass
 else:raise AssertionError('negative balance permitted')
 check(db.execute('SELECT coins FROM wallet').fetchone()[0]==expected-25,'failed purchase rolls back')
 db.close();db=sqlite3.connect(path);check(db.execute('SELECT stars FROM wallet').fetchone()[0]==1200,'reopen wallet');check(db.execute("SELECT item_id FROM equipped WHERE category='skin'").fetchone()[0]=='sky','reopen equipped');check(db.execute('SELECT COUNT(*) FROM ledger').fetchone()[0]==1201,'ledger one per award/purchase')
 with db:
  for statement in (root/'app/src/main/res/raw/reset.sql').read_text().split(';'):
   if statement.strip():db.execute(statement)
 check(db.execute('SELECT coins,stars FROM wallet').fetchone()==(0,0),'reset balances')
 for table in ['progress','completion','inventory','equipped','ledger']:
  check(db.execute('SELECT COUNT(*) FROM '+table).fetchone()[0]==0,'reset '+table)
 db.close()
java='\n'.join(p.read_text() for p in (root/'app/src/main/java').rglob('*.java'))
manifest=(root/'app/src/main/AndroidManifest.xml').read_text()
for forbidden in ['WebView','ACTION_VIEW','market://','https://','getLaunchIntentForPackage','com.zeptolab','org.khankids','INTERNET','uses-permission']:
 check(forbidden not in java+manifest,'forbidden runtime dependency '+forbidden)
check('dependencies {' not in (root/'app/build.gradle').read_text(),'no runtime library dependencies')
check('Color.WHITE' in (root/'app/src/main/java/com/littlemakers/village/nativeapp/ThemeManager.java').read_text(),'white dark text')
print(json.dumps({'status':'PASS','checks':checks,'contrast_ratios':contrasts,'scope':'Schema transactions, palette contrast and native source audit; no Android device execution'},indent=2))
