# ModelMeImpressed
Wurm Unlimited mod to change the model of any Item/Creature/Player(later referred to as object) on the fly

# Features
Right click on any object, choose the action "Remodel" and transform it to whatever model you choose

Right click on any object, choose the action "Random model" and transform it to a random model

You can now reset each object's model to the original by right clicking on it and choosing reset model, or choosing browse and reviewing each custom model change

Flexibility: If the mod causes any issue/is not needed anymore, it may just be deleted and its effects will completely disappear. The table in the modsupport database will be left behind. This is not an issue.

# ~Bugs~ Features 2.0
With <a href="https://github.com/bdew-wurm/threedee">threedee</a> placing something on an item and then changing its model will leave the item in place where you probably could not put it on the new model, meaning you can place items <a href="https://wurmcw.ddns.net/images/after.png">where previously impossible</a>

~Transforming a felled tree apparently causes the new models to be huge, <a href="https://wurmcw.ddns.net/images/unicornforscale.png">and I mean HUGE</a>~ nvm you can do this by changing the item's auxdata

# What models are available?
The mod contains every model found in the mappings.txt in the graphics.jar of the client. I am sure it is not perfect. Some are probably missing, because the devs forgot to add it, and some are probably broken. I have not personally tried out all the ~3K models available. Please open an issue or message me on discord(Tyoda#5412) if you find a problem.

You may also add any custom models to it via the "custom_pretty", and "custom_model" fields in the .properties file.

# Planned features
These are features I plan to add (if they are truly viable), open an issue if you really want one and I'll start working on it!
 
 - The models lists are messy, fix that somehow I guess

 - Automatically generate model list from some resource in the game.

 - ~Add a text input to the Remodel action where you can easily enter any model you know~ Done

 - ~Two more actions: 1. Set back to normal model 2. Set to random model~ Done

 - ~Walls could probably be transformed just the same~ Not possible

 - ~Solve having to push/rotate items after remodel~ Done

 - ~Solve player having to relog to see its own model change~ Done

# What I can't do
 - Getting the model name for structures(Walls, Floors, Bridges, Fences) is handled entirely client-side.
