package com.littlemakers.village.nativeapp;

/** Original, bundled lesson and story scripts. No remote content or generative runtime. */
final class ExplorerCatalog {
 static final String[] NAMES={"Discovery Trail","Letter Adventures","Learning Islands","Little Makers Friends","Shape Builders","Maker World"};
 static final class Lesson {
  final String prompt,a,b,c,hint; final int answer,model,count,color;
  Lesson(String p,String a,String b,String c,int answer,String hint,int model,int count,int color){this.prompt=p;this.a=a;this.b=b;this.c=c;this.answer=answer;this.hint=hint;this.model=model;this.count=count;this.color=color;}
  String[] choices(){return new String[]{a,b,c};}
 }
 static final Lesson[] LESSONS={
  new Lesson("How many apples are in this basket?","2","3","4",1,"Count each apple once.",StoryModels.APPLE,3,0xe99788),
  new Lesson("What helps a seed grow?","Water and light","A closed box","Dry paper",0,"A plant uses water and sunlight.",StoryModels.SEEDLING,1,0x83bd92),
  new Lesson("Which word starts like sun?","Moon","Sock","Fish",1,"Listen to the first sound: sss.",StoryModels.SUN,1,0xf1ce76),
  new Lesson("A friend looks sad. What can we ask?","Why are you slow?","Go away?","Would you like help?",2,"Offer a kind question without guessing their feelings.",StoryModels.BEAR,1,0xd7ab89),
  new Lesson("How many sides does a triangle have?","3","4","5",0,"Trace its three straight sides.",-1,1,0x82b7d1),
  new Lesson("We have two flowers and add one. How many now?","2","4","3",2,"Count on one from two.",StoryModels.FLOWER,3,0xf1c879),
  new Lesson("Which animal belongs in water?","Rabbit","Fish","Bear",1,"Look for an animal with fins.",StoryModels.FISH,1,0x80bba8),
  new Lesson("Which object gives us shade?","Tree","Shell","Ball",0,"Look for a leafy canopy.",StoryModels.TREE,1,0x75ad87),
  new Lesson("Which word rhymes with boat?","Sun","Cat","Goat",2,"Boat and goat have the same ending sound.",StoryModels.BOAT,1,0xe6bd78),
  new Lesson("What comes after 4 when counting?","3","5","7",1,"Say one, two, three, four, then the next number.",StoryModels.STAR,5,0xf1cd76),
  new Lesson("What do we use on a rainy walk?","Umbrella","Telescope","Paintbrush",0,"It keeps rain off our head.",StoryModels.UMBRELLA,1,0xa3b7dc),
  new Lesson("Two friends want one brush. What could help?","Hide it","Grab it","Make a turn-taking plan",2,"A plan can give both friends a turn.",StoryModels.BRUSH,1,0x86b99c),
  new Lesson("Which set has an even number of shells?","3 shells","4 shells","5 shells",1,"Make pairs with no shell left over.",StoryModels.SHELL,4,0xe4bd9b),
  new Lesson("What can a caterpillar become?","Butterfly","Stone","Fish",0,"It changes inside a chrysalis.",StoryModels.BUTTERFLY,1,0xe5b08d),
  new Lesson("Which word begins with m?","Tree","Moon","Boat",1,"Hum the sound mmm.",StoryModels.MOON,1,0xf0db9b),
  new Lesson("Four carrots, then one is eaten. How many remain?","5","2","3",2,"Take away one from four.",StoryModels.CARROT,3,0xe8a36b),
  new Lesson("Where should a picture book go after reading?","In the pond","On a dry shelf","Under the rain",1,"Keeping books dry protects their pages.",StoryModels.BOOK,1,0x8db3d4),
  new Lesson("A turtle is crossing our path. What can we do?","Give it space","Pull its shell","Block its way",0,"Observe gently without touching a wild animal.",StoryModels.TURTLE,1,0x85b695),
  new Lesson("Which shape has no corners?","Triangle","Square","Circle",2,"A circle has one round edge.",-2,1,0xdbb686),
  new Lesson("Which is a fruit?","Carrot","Apple","Shell",1,"An apple grows around seeds on a tree.",StoryModels.APPLE,1,0xe79c86),
  new Lesson("What comes next: red, blue, red, blue...?","Red","Green","Yellow",0,"Repeat the same two-color pattern.",StoryModels.FLOWER,4,0xdb8b90),
  new Lesson("Which tool helps us look at distant stars?","Basket","Brush","Telescope",2,"This tool collects light from far away.",StoryModels.TELESCOPE,1,0xa2b6d1),
  new Lesson("We find litter beside a pond. What is a safe plan?","Ask an adult to help clean it","Throw it in the water","Leave more litter",0,"Ask an adult before handling unknown rubbish.",StoryModels.FISH,1,0x85baa6),
  new Lesson("A puzzle is tricky. What can we try?","We must rush","Break it into small steps","Never ask for help",1,"One small step can help us find the next.",StoryModels.KITE,1,0xe8b67f)
 };
 static final String[] WORDS={"apple","boat","carrot","dog","egg","fish","goat","hat","insect","jam","kite","leaf","moon","nest","octopus","pig","queen","rabbit","sun","turtle","umbrella","van","water","fox","yarn","zebra"};
 static final String[] SOUNDS={"Short a, as in apple","b, as in boat","Hard c, as in carrot","d, as in dog","Short e, as in egg","f, as in fish","g, as in goat","h, as in hat","Short i, as in insect","j, as in jam","k, as in kite","l, as in leaf","m, as in moon","n, as in nest","Short o, as in octopus","p, as in pig","qu, as in queen","r, as in rabbit","s, as in sun","t, as in turtle","Short u, as in umbrella","v, as in van","w, as in water","The ending sound ks, as in fox","y, as in yarn","z, as in zebra"};
 static final String[] BUILD_WORDS={"CAT","DOG","SUN","HAT","MAP","BED","CUP","PIN","LOG","NET","BUG","FOX"};
 static final int[] LETTER_MODELS={22,21,16,19,19,7,19,19,25,19,23,20,3,0,19,19,19,28,18,15,17,19,34,19,19,19};
 static final class Tale {
  final String title,setup,a,b,resultA,resultB,question,c,d,endingC,endingD;final int prop;
  Tale(String title,String setup,String a,String b,String ra,String rb,String q,String c,String d,String ec,String ed,int prop){this.title=title;this.setup=setup;this.a=a;this.b=b;resultA=ra;resultB=rb;question=q;this.c=c;this.d=d;endingC=ec;endingD=ed;this.prop=prop;}
 }
 static final Tale[] TALES={
  new Tale("The shared brush","Pip and Mica both reach for one paintbrush.","Ask Mica what she wants to paint","Offer the first turn","Mica wants to paint a blue river.","Mica paints her river, then hands Pip the brush.","How could they finish one picture together?","Take turns painting the sky","Choose a part for each friend","Their sky fills with two kinds of clouds.","Pip paints trees while Mica adds a river.",StoryModels.BRUSH),
  new Tale("The quiet picnic","Mica covers her ears at a noisy picnic.","Ask if she wants a quieter spot","Offer to walk beside her","Mica points to a peaceful tree.","They walk slowly away from the noisy blanket.","What could make the new spot comfortable?","Spread a soft blanket","Ask Mica where to sit","They enjoy their lunch in the shade.","Mica chooses a place with a view of the pond.",StoryModels.BASKET),
  new Tale("The fallen tower","Pip's wooden tower tumbles. Pip takes a deep breath.","Ask if Pip wants company","Offer help picking up pieces","Pip asks Mica to sit nearby.","Together they gather the scattered blocks.","How might Pip try building again?","Start with a wide base","Build a small tower first","The wide base holds their new tower steady.","A small tower gives Pip a fresh starting point.",StoryModels.HOUSE),
  new Tale("A place in the game","A new friend watches a ball game from the path.","Ask if they would like to join","Explain the game gently","The friend smiles and asks how to play.","The friend learns where the ball should go.","What can help everyone feel welcome?","Practice one turn together","Let the new friend choose a role","A practice turn helps the new friend feel ready.","The friend chooses to roll the ball first.",StoryModels.STAR),
  new Tale("The windy kite","A gust puts Mica's kite in a low branch.","Ask an adult for help","Stay back and talk about a plan","An adult brings the kite down safely.","They decide an adult can help reach it.","Where could the kite fly next?","An open field away from trees","A clear spot chosen with an adult","The kite dances over the open field.","They check the space together before flying.",StoryModels.KITE),
  new Tale("The last strawberry","One strawberry is left in the picnic basket.","Ask who would like some","Offer to share it","Both friends say they would enjoy a taste.","Mica thanks Pip for offering.","What is a fair plan?","Ask an adult to cut two pieces","Save it for their next shared snack","Two little pieces make a shared snack.","They pack it safely for later and choose another snack.",StoryModels.STRAWBERRY),
  new Tale("A different idea","Pip wants a moon picture. Mica wants a sunny garden.","Listen to Mica's idea","Explain what Pip likes about the moon","Mica imagines bright flowers and warm light.","Pip imagines stars reflected in the pond.","How could both ideas fit?","Make a day-and-night picture","Make two pages of one book","Their picture moves from sunshine to moonlight.","Their book shows the same garden at two times.",StoryModels.MOON),
  new Tale("The missing book","Mica cannot find the book she borrowed from Pip.","Tell Pip what happened","Ask Pip to help retrace the walk","Pip is glad Mica told the truth.","They remember reading beside the pond.","Where should they begin looking?","The last reading spot","The shelf they used before","The book is safe on the picnic blanket.","They find the book on the lower shelf.",StoryModels.BOOK),
  new Tale("The tiny visitor","A butterfly rests beside their unfinished garden.","Watch from a little distance","Make room around the flower","The butterfly slowly opens its wings.","The flower stays clear so the butterfly can rest.","How could the garden welcome visitors?","Plant more flowers with an adult","Keep a quiet observation spot","More blossoms bring color to the garden.","Their observation spot lets them watch without touching.",StoryModels.BUTTERFLY),
  new Tale("Trying a new path","Pip feels unsure about a stepping-stone trail.","Ask which step feels comfortable","Offer to practice beside the path","Pip chooses the widest stone first.","Pip practices the movement on flat ground.","What should happen next?","Let Pip choose when to continue","Take a rest together","Pip chooses one more small step.","They rest, and the trail will still be there later.",StoryModels.PLANK),
  new Tale("The surprise shell","Mica finds a shell that may have a creature inside.","Look without picking it up","Ask an adult what to do","A tiny movement shows the shell is a home.","The adult suggests leaving the shell where it is.","What can they keep from this discovery?","Draw the shell in a notebook","Tell Pip what they noticed","Mica draws a spiral to remember the shell.","Their story helps another friend notice small homes.",StoryModels.SHELL),
  new Tale("The thank-you garden","Pip helped water Mica's seedlings all week.","Say what Pip's help meant","Offer to help with Pip's garden","Pip smiles when Mica thanks him for the watering.","Pip invites Mica to plant a flower together.","How could they celebrate their work?","Look closely at the new leaves","Draw a picture of the garden","They notice two new leaves on the smallest plant.","Their picture remembers the garden they cared for together.",StoryModels.SEEDLING)
 };
 static int count(int mode){return new int[]{LESSONS.length,38,24,TALES.length,24,12}[mode];}
 static String prefix(int mode){return new String[]{"discovery","letters","island","friends","tangram","maker"}[mode];}
 // Map stops link to actual lesson stages. A visit never counts as a completion.
 static final int[] STOP_MODES={-1,-2,-3,0,1,3,4,5};
 static int stopMode(int stop){return STOP_MODES[stop%8];}
 static int stopStage(int stop){return stop/8;}
 static String stopKey(int stop){int mode=stopMode(stop);return "creative-"+(mode<0?new String[]{"guardian","balance","robot"}[-mode-1]:prefix(mode))+"-"+stopStage(stop);}
 private ExplorerCatalog(){}
}
