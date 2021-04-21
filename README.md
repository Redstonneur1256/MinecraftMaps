# MinecraftMaps
MinecraftMaps is a plugin used to create interactive in game displays.

You can manually update a display by calling `Display#update()` or `Display#update(Player)` for updating it for only one player, please note that `Display#update(Player)` will not update the render if the mode is global, only sync the display with the player.

A display will be black if he has not renderers, to add one just use `Display#addRender(Renderer)`. The renderer interface has one method witch receive one BufferedImage and one Player when the render need to be updated. Note that if the mode is global the player will be `null`.

Display can be used by players, for listening when a player will tap the display, use `Display#addListener(TouchListener)`. The listener will receive, the display where it has been touched, the player, the position X,Y in pixels on the display, and a boolean true if it is a left click or false if it is a right click.

How to use in my project:
=====
- Install the Plugin on your server, find it on [SpigotMC](https://www.spigotmc.org/resources/minecraftmaps.84639/) or build it from this repository
- Add the plugin to your project:
  - Manually: Add the jar to your project dependencies
  - Using gradle:
    * Add jitpack to your project repositories:
      ```groovy
      repositories {
        maven { url = 'https://jitpack.io' }
      }
      ```
    * Add the project as dependency:
      ```groovy
      dependencies {
        compileOnly 'com.github.Redstonneur1256.MinecraftMaps:API:1.1'
      }
      ```
  

To obtain the DisplayManager:
=====

- Bukkit:
```java
DisplayManager<Player> displayManager = Bukkit.getServicesManager().getRegistration(DisplayManager.class).getProvider();
```


Some examples:
=====

Please note that <Player> type will depend on your server, `org.bukkit.entity.Player` for bukkit, ect...

* Display your screen in game:
````java
// Get a full screen size
Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
Rectangle rectangle = new Rectangle(0, 0, dimension.width, dimension.height);

// Create a robot that will record screen
Robot robot = new Robot(); 

// Create a display of 3 blocks width and 2 blocks high starting from map 0 to (3x2) 6
Display<Player> display = displayManager.createDisplay(3, 2, (short) 0, RenderMode.GLOBAL);

// Add a renderer
display.addRenderer((image, graphics, player) -> { // Here player will be null because the Display mode is GLOBAL
    // Create a screen capture:
    BufferedImage capture = robot.createScreenCapture(rectangle);
    // Render it on the display:
    graphics.drawImage(capture, 0, 0, image.getWidth(), image.getHeight(), null);
});
// Automatically update the display every tick
display.updateAtFixedRate(1, false); 
````
* A button:
```java
// Very very simple display with only one map:
Display<Player> display = displayManager.createDisplay(1, 1, (short) 0, RenderMode.global);
display.addRenderer(new Renderer() { // Can be replaced by a lambda
     @Override
     public void render(BufferedImage image, Graphics2D graphics, Player player) {
          graphics.drawString("Tap me !", 20, 20);
     }
});
display.addListener(new TouchListener() { // Can be replaced by a lambda
    @Override
    public void onTouch(Display display, Player player, int x, int y, boolean left) {
        player.sendMessage("You click on the display at position " + x + " " + y + "!");
    }
});
```
