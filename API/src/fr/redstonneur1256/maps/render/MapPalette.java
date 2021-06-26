package fr.redstonneur1256.maps.render;

import fr.redstonneur1256.maps.utils.Logger;
import fr.redstonneur1256.redutilities.graphics.Palette;

import java.awt.*;
import java.io.*;

public class MapPalette {

    private static byte[] palette;

    public static void generatePalette(InputStream input, OutputStream output) throws IOException {
        Palette<IDContainer> palette = new Palette<>();

        try(DataInputStream stream = new DataInputStream(input)) {
            int count = stream.readInt();

            for(int i = 0; i < count; i++) {
                int red = stream.readShort();
                int green = stream.readShort();
                int blue = stream.readShort();

                if(i >= 4) {
                    palette.addColor(new IDContainer(new Color(red, green, blue), (byte) i));
                }
            }
        }

        int count = (0xFF << 16 | 0xFF << 8 | 0xFF) + 1;
        int chunkSize = 1_000_000;

        StringBuilder builder = new StringBuilder();

        byte[] bytes = new byte[count];

        for(int i = 0; i < count; i += chunkSize) {
            double progress = (double) i / count;
            int length = (int) (progress * 30);
            builder.setLength(0);
            builder.append('[');
            for(int j = 0; j < 30; j++) {
                builder.append(j < length ? '|' : ' ');
            }
            builder.append("] ").append((int) (progress * 100)).append('%');
            Logger.log(builder.toString());

            for(int j = i, h = Math.min(i + chunkSize, count); j < h; j++) {
                bytes[j] = palette.matchColor(j).getValue();
            }
        }

        try(DataOutputStream stream = new DataOutputStream(output)) {
            stream.writeInt(count);
            stream.write(bytes);
        }
    }

    public static void loadPalette(InputStream input) throws IOException {
        if(palette != null) {
            throw new IllegalStateException("Palette already has been loaded !");
        }

        try(DataInputStream stream = new DataInputStream(input)) {

            byte[] palette = new byte[stream.readInt()];
            stream.readFully(palette);

            MapPalette.palette = palette;
        }
    }

    public static byte[] getPalette() {
        return palette;
    }

    private static class IDContainer extends Palette.ColorContainer {

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
