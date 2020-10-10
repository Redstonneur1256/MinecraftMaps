package fr.redstonneur1256.maps.maps;

import fr.redstonneur1256.redutilities.graphics.Palette;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MapColors {

    public static final Palette<IntContainer> palette;

    static {
        palette = new Palette<IntContainer>().useCache(true);
    }

    public static void load(InputStream inputStream) throws IOException {
        DataInputStream input = new DataInputStream(inputStream);
        int count = input.readInt();

        for(int i = 0; i < count; i++) {

            int red = input.readShort();
            int green = input.readShort();
            int blue = input.readShort();

            if(i < 4) { // Transparent colors are not supported by the palette
                continue;
            }

            palette.addColor(new IntContainer(new Color(red, green, blue), i));
        }

        input.close();
    }

    public static void clearCache() {
        palette.clearCache();
    }

    public static class IntContainer extends Palette.ColorContainer {
        public final byte id;

        public IntContainer(Color color, int id) {
            super(color);
            this.id = (byte) id;
        }
    }

}
