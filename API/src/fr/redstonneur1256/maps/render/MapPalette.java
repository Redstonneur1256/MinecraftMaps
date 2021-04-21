package fr.redstonneur1256.maps.render;

import fr.redstonneur1256.redutilities.graphics.Palette;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MapPalette {

    private static Palette<IDContainer> palette;

    public static void loadPalette(InputStream input, boolean useCache) throws IOException {
        if(palette != null) {
            throw new IllegalStateException("Palette already has been loaded !");
        }

        try(DataInputStream stream = new DataInputStream(input)) {
            Palette<IDContainer> palette = new Palette<>();

            int count = stream.readInt();

            for(int i = 0; i < count; i++) {
                int red = stream.readShort();
                int green = stream.readShort();
                int blue = stream.readShort();

                // Transparent colors are not supported by the palette & the first 4 colors are transparent
                if(i < 4) {
                    continue;
                }

                Color color = new Color(red, green, blue);

                palette.addColor(new IDContainer(color, (byte) i));
            }

            palette.useCache(useCache);
            MapPalette.palette = palette;
        }
    }

    public static Palette<IDContainer> getPalette() {
        return palette;
    }

    public static class IDContainer extends Palette.ColorContainer {

        private byte value;

        public IDContainer(Color color, byte value) {
            super(color);
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

    }

}
