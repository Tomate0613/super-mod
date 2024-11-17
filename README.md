# SUPER MOD

## Computers

### Important notes

- All Supermod computers are run completely client sided
- Run `upload` to sync back changes made to the local file system
- You can add files to the filesystem either by running `tac [filename] [contents]` or by dragging and dropping the file
  onto the computer.
  - Remember to sync the fs afterward
- Computers are not run on a separate threat. This means that all blocking code will freeze the game.
  - To combat infinite loops computers have a maximum amount of instructions set
- `init.lua` will get run when a screen controller block is loaded or a computer is clicked

### Library

Most of the default lua library is usable as is, but supermod also provides additional features:

- Note: Reading from `System.in` will completely freeze your game. Don't do that!
- Note: All fs operations are executed on the virtual file system. This includes spawning other processes

#### Puter Lib

- `puter.on(string event, fn listener)` Allows you to listen for available events:
  - `tick`
  - `render` `float tickDelta`
  - `on_key_pressed` `int key, int scancode, int modifier`
  - `on_char_typed` `char character `
  - `screen_interact_nearby` `Position interactPos, Position interactBlockPos, Position computerPos`
- `puter.stop()` Stops the currently running lua program
- `puter.run(string name, List args)` Runs a lua program
- `List<string> puter.list_files()` Lists all files on the fs
- `{int x, int y} puter.get_screen_size()` Returns the screen size in blocks
- `puter.make_sound(string sound, float volume, float pitch)` Make a sound
- `Position puter.get_pos()` Returns the position of the computer block
- `puter.upload()` Syncs the fs with the server. Requires op

#### MinecraftLib
- `Player minecraft.get_local_player()`
- `Scoreboard minecraft.get_scoreboard(string objective)` Note that only synced objectives are visible to computers.

#### SuperModLib
- `double supermod.get_speed()`
- `supermod.request_session(string profile, fn callback)` Request a super session to start for the player
- `supermod.mc_fn(string function_name)` Run a minecraft function in the `super_mod` namespace

### Tables

- `Position`
  - `double|int x`
  - `double|int y`
  - `double|int z`
- `Player`
  - `string name`
- `Position position`
- `bool is_shift_key_down`
- `Profile`
  - `string area`
  - `double itemUsageInfluence`
  - `double jumpingInfluence`
  - `double offset`
  - `double rotInfluence`
  - `double speedInfluence`
- `Objective`
  - `List scores`
    - `string owner`
    - `int value`
  - `string name`
  - `string render_type`
  - `string criteria`
