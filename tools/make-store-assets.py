"""Original native icon export and feature graphic. Not screenshot generation."""
from pathlib import Path
import xml.etree.ElementTree as ET
from PIL import Image,ImageDraw,ImageFont
root=Path(__file__).resolve().parents[1];ns='{http://schemas.android.com/apk/res/android}'
v=ET.parse(root/'app/src/main/res/drawable/app_icon.xml').getroot()
svg='<svg xmlns="http://www.w3.org/2000/svg" width="108" height="108" viewBox="0 0 108 108">'+''.join('<path fill="'+p.attrib[ns+'fillColor']+'" d="'+p.attrib[ns+'pathData']+'"/>' for p in v)+'</svg>'
p=root/'release/media';p.mkdir(exist_ok=True);(p/'logo-master.svg').write_text(svg)
# Same original paths as app_icon.xml, rasterized with supersampling.
s=2048/108;icon=Image.new('RGBA',(2048,2048),'#574491');d=ImageDraw.Draw(icon)
box=lambda coords:tuple(x*s for x in coords)
d.rounded_rectangle(box((28,26,80,83)),radius=14*s,fill='#58C9B4')
d.rectangle(box((40,43,47,51)),fill='#202941');d.rectangle(box((61,43,68,51)),fill='#202941')
def curve(a,b,c):return [((1-t)**2*a[0]+2*(1-t)*t*b[0]+t*t*c[0],(1-t)**2*a[1]+2*(1-t)*t*b[1]+t*t*c[1]) for t in [i/40 for i in range(41)]]
points=curve((40,62),(54,76),(68,62))+[(64,59)]+curve((64,59),(54,67),(44,59))
d.polygon([(x*s,y*s) for x,y in points],fill='white');icon=icon.resize((512,512),Image.Resampling.LANCZOS);icon.save(p/'play-icon-512.png')
image=Image.new('RGB',(1024,500),'#F6F4FF');d=ImageDraw.Draw(image)
d.rounded_rectangle((25,25,999,475),radius=36,fill='#EBE7FC',outline='#C5C2DF',width=3)
small=icon.resize((270,270),Image.Resampling.LANCZOS);image.paste(small,(65,115),small)
font=lambda size:ImageFont.truetype(str(root/'app/src/main/res/font/makers_text.ttf'),size)
for x,y,text,size in [(375,100,'Little Makers',49),(375,190,'Play and practice',29),(375,252,'Offline activities',25),(375,302,'Ages 4 to 8',25)]:d.text((x,y),text,font=font(size),fill='#202941')
image.save(p/'feature-graphic-1024x500.png')
print('Exported native-matching original icon and feature graphic; not screenshots.')
