"""Original native icon export and feature graphic. Not screenshot generation."""
from pathlib import Path
import runpy
from PIL import Image,ImageDraw,ImageFont
root=Path(__file__).resolve().parents[1]
runpy.run_path(str(root/'tools/export-brand.py'),run_name='__main__')
p=root/'release/media';p.mkdir(exist_ok=True)
icon=Image.open(p/'play-icon-512.png').convert('RGBA')
image=Image.new('RGB',(1024,500),'#F6F4FF');d=ImageDraw.Draw(image)
d.rounded_rectangle((25,25,999,475),radius=36,fill='#EBE7FC',outline='#C5C2DF',width=3)
small=icon.resize((270,270),Image.Resampling.LANCZOS);image.paste(small,(65,115),small)
def font(size):
 for name in ['DejaVuSans-Bold.ttf','arial.ttf',str(root/'app/src/main/res/font/makers_text.ttf')]:
  try:return ImageFont.truetype(name,size)
  except OSError:pass
 return ImageFont.load_default()
for x,y,text,size in [(375,100,'Little Makers',49),(375,190,'Play and practice',29),(375,252,'Offline activities',25),(375,302,'Ages 4 to 8',25)]:d.text((x,y),text,font=font(size),fill='#202941')
image.save(p/'feature-graphic-1024x500.png')
print('Exported native-matching original icon and feature graphic; not screenshots.')
