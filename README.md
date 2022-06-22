# ModelMeImpressed
Wurm Unlimited mod to change the model of any Item/Creature/Player on the fly

# Features
Right click on any Item/Creature(/Player), choose the action "Remodel" and transform it to whatever model you choose

Flexibility: If the mod causes any issue/is not needed anymore, it may just be deleted and its effects will completely disappear. The database table in the modsupport will be left behind, this is not much of an issue.

# ~Bugs~ Features 2.0
With <a href="https://github.com/bdew-wurm/threedee">threedee</a> placing something on an item and then changing its model will leave the item in place, where you probably could not put it on the new model, meaning you can place items <a href="https://wurmcw.ddns.net/images/after.png">where previously impossible</a>

Transforming a felled tree apparently causes the new models to be huge, <a href="https://wurmcw.ddns.net/images/unicornforscale.png">and I mean HUGE</a>

# What models are available?
The mod contains every model found in the mappings.txt in the graphics.jar of the client. I am sure it is not perfect. Some are probably missing, because the devs forgot to add it, and some are probably broken. I have not personally tried out all the ~6K models available. Please open an issue or message me on discord(Tyoda#5412) if you find a problem.

You may also add any custom models to it via the "custom_pretty", and "custom_model" fields in the .properties file.

# Planned features
These are features I plan to add (if they are truly viable), open an issue if you really want one and I'll start working on it!

 - Two more actions: 1. Set back to normal model 2. Set to random model

 - Add a text input to the Remodel action where you can easily enter any model you know

 - ~Walls could probably be transformed just the same~ Not possible

 - ~Solve having to push/rotate items after remodel~ Done

 - Automatically generate model list from some resource in the game.

 - ~Solve player having to relog to see its own model change~ Done
 
 - The models lists are messy, fix that somehow I guess

# What I can't do
 - Getting the model name for structures(Walls, Florrs, Bridges, Fences) is handled entirely client-side. These 
