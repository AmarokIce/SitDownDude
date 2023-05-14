package club.someoneice.sitdown;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import committee.nova.sittable.common.event.impl.SittableRegisterEvent;
import committee.nova.sittable.common.registry.type.SittableRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mod(SitDownDude.MODID)
public class SitDownDude {
    public static final String MODID = "sit_down";

    private static Map<Block, SitDownData> readBlockFromDatalist() {
        Map<Block, SitDownData> map = Maps.newHashMap();
        File file = new File(System.getProperty("user.dir") + File.separator + "config" + File.separator + "sitdowndude.json");
        if (!file.isFile()) {
            try { file.createNewFile(); } catch (IOException ignored) {}
            return map;
        }

        try {
            byte[] stringText = new byte[((Long) file.length()).intValue()];
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(stringText);
            inputStream.close();

            String text = new String(stringText, StandardCharsets.UTF_8);
            List<SitDownData> dataList = new Gson().fromJson(text, new TypeToken<List<SitDownData>>() {}.getType());
            if (!dataList.isEmpty()) {
                for (SitDownData data : dataList) {
                    Block block = Registry.BLOCK.get(new ResourceLocation(data.block));
                    if (block != Blocks.AIR) map.put(block, data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    @SubscribeEvent
    public void onRegister(SittableRegisterEvent event) {
        Map<Block, SitDownData> oMap = readBlockFromDatalist();
        for (Block block : oMap.keySet()) {
            SitDownData data = oMap.get(block);
            double x = data.x == null ? 0.5D : data.x;
            double y = data.y == null ? 0.5D : data.y;
            double z = data.z == null ? 0.5D : data.z;
            Vector3d vec = new Vector3d(x, y, z);
            event.registerSittable(new SittableRegistry(block, (bk, m, o) -> Optional.of(vec)));
        }
    }

    static class SitDownData {
        String block;
        @Nullable Double x;
        @Nullable Double y;
        @Nullable Double z;

        public SitDownData(String block, @Nullable Double x, @Nullable Double y, @Nullable Double z) {
            this.block = block;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
