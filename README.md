# MinecraftMaps
MinecraftMaps is a plugin used to create interactive in game displays.

Creating a display took in parameters the width and height in blocks, the maps starting id, and a mode (global/player)
````java
Display myDisplay = new Display(width, height, mapsStart, mode);
````
You can manually update a display by calling `Display#update()` or `Display#update(Player)` for updating it for only one player, please note that `Display#update(Player)` will not update the render if the mode is global, only sync the display with the player.

A display will be black if he has not renderers, to add one just use `Display#addRender(Renderer)`. The renderer interface has one method witch receive one BufferedImage and one Player when the render need to be updated. Note that if the mode is global the player will be `null`.

Display can be used by players, for listening when a player will tap the display, use `Display#addListener(TouchListener)`. The listener will receive, the display where it has been touched, the player, the position X,Y in pixels on the display, and a boolean true if it is a left click or false if it is a right click.

# How to use in my project:
- Install the Plugin on your server, find it on [SpigotMC](https://www.spigotmc.org/resources/minecraftmaps.84639/) or build it from this repository
- Add the plugin to your project:
  - Manually: Add the jar to your project dependencies
  - Using gradle:
    - Clone the repository on your computer
    - Run the following commands to update & install it:
      - `git pull`
      - `gradlew publishMavenPublicationToMavenLocal`
    - Add `mavenLocal()` to your repositories and add `compileOnly fr.redstonneur1256:MinecraftMaps:x.y.z` to your dependencies
  

# Some examples:

* Display your screen in game:
````java
// Get a full screen size
Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
Rectangle rectangle = new Rectangle(0, 0, dimension.width, dimension.height);

// Create a robot that will record screen
Robot robot = new Robot(); 

// Create a display of 3 blocks width and 2 blocks high starting from map 0 to (3x2) 6
Display display = new Display(3, 2, (short) 0, Mode.global);

// Add a renderer
display.addRenderer((bufferedImage, player) -> { // Here player will be null because the Display mode is Global
    BufferedImage image = robot.createScreenCapture(rectangle);
    Graphics graphics = bufferedImage.getGraphics(); // The BufferedImage graphics is screenWidth*128, screenHeight*128 px
    graphics.drawImage(image, 0, 20, 384, 216, null); // Draw the image on the screen with 16:9 ratio
    graphics.dispose(); // Dispose the graphics
});
display.updateAtFixedRate(2, true); // Update the display every 2 tick (10 FPS) with true so it will be asynchronous and will not lag the server
````
* A button:
```java
// Very very simple display with only one map:
Display display = new Display(1, 1, (short) 0, Mode.global);
display.addRenderer(new Renderer() { // Can be replaced by a lambda
     @Override
     public void render(BufferedImage image, Player player) {
          Graphics graphics = image.getGraphics();
          graphics.drawString("Tap me !", 20, 20);
          graphics.dispose();
     }
});
display.addListener(new TouchListener() { // Can be replaced by a lambda
    @Override
    public void onTouch(Display display, Player player, int x, int y, boolean left) {
        player.sendMessage("You click on the display at position " + x + " " + y + "!");
    }
});
```
