"""Export the original native vector logo to SVG and Play Store PNG. Dev dependency: cairosvg."""
from pathlib import Path
import xml.etree.ElementTree as E
import html
r=Path(__file__).resolve().parents[1];ns='{http://schemas.android.com/apk/res/android}'
root=E.parse(r/'app/src/main/res/drawable/brand_mascot.xml').getroot();paths=[];gradients=[]
for i,p in enumerate(root.findall('path')):
 fill=p.get(ns+'fillColor','#000000');g=p.find('.//gradient')
 if g is not None:
  gid=f'gradient{i}';fill=f'url(#{gid})';stops=''.join(f'<stop offset="{x.get(ns+"offset")}" stop-color="{x.get(ns+"color")}"/>' for x in g.findall('item'));gradients.append(f'<linearGradient id="{gid}" gradientUnits="userSpaceOnUse" x1="{g.get(ns+"startX")}" y1="{g.get(ns+"startY")}" x2="{g.get(ns+"endX")}" y2="{g.get(ns+"endY")}">{stops}</linearGradient>')
 attrs={'fill':fill,'d':p.get(ns+'pathData'),'opacity':p.get(ns+'fillAlpha','1')}
 for android,svg in [('strokeColor','stroke'),('strokeWidth','stroke-width')]:
  if p.get(ns+android):attrs[svg]=p.get(ns+android)
 paths.append('<path '+' '.join(f'{k}="{html.escape(v)}"' for k,v in attrs.items())+'/>')
svg='<svg xmlns="http://www.w3.org/2000/svg" width="512" height="512" viewBox="0 0 108 108"><defs><linearGradient id="brand" x2="1" y2="1"><stop stop-color="#286F79"/><stop offset="1" stop-color="#343D72"/></linearGradient>'+''.join(gradients)+'</defs><rect width="108" height="108" fill="url(#brand)"/>'+''.join(paths)+'</svg>'
(r/'art-source/logo-2.5.0.svg').write_text(svg)
(r/'release/media/logo-master.svg').write_text(svg)
try:
 import cairosvg
 cairosvg.svg2png(bytestring=svg.encode(),write_to=str(r/'release/media/play-icon-512.png'),output_width=512,output_height=512)
 print('Exported original SVG and 512px PNG logo.')
except ImportError:print('SVG exported. Install cairosvg to also render the PNG.')
