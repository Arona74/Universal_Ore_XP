package net.anoltongi.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.block.Block;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/*
  A data‑driven loader that watches data/universal_ore_xp/ore_xp/*.json
  and builds a Block to IntRange map for your ore to XP logic.
 */
public class OreXPData implements SimpleSynchronousResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger("OreXPData");
    private static final Identifier ID = new Identifier("universal_ore_xp", "ore_xp_data");
    public static final OreXPData INSTANCE = new OreXPData();

    private final Map<Block, IntRange> map = new HashMap<>();

    private OreXPData() {}

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    /*
      Fired on world startup and whenever `/reload` runs.
      Scans all JSON under `data/universal_ore_xp/ore_xp/*.json`.
     */
    @Override
    public void reload(ResourceManager manager) {
        map.clear();
        //We search for all JSON files under our specified path.
        manager.findResources("ore_xp", id -> id.getPath().endsWith(".json"))
                .forEach((id, resource) -> {
                    try (InputStream in = resource.getInputStream();
                         InputStreamReader reader = new InputStreamReader(in)) {

                        JsonElement root = JsonParser.parseReader(reader);
                        // if an array, loop it:
                        if (root.isJsonArray()) {
                            for (JsonElement elem : root.getAsJsonArray()

                            ) {
                                if (!elem.isJsonObject()) continue;
                                loadEntry(elem.getAsJsonObject(), id);
                            }
                        }
                        // If a single object, we just load it:
                        else if (root.isJsonObject()) {
                            loadEntry(root.getAsJsonObject(), id);
                        }

                    } catch (Exception e) {
                        LOGGER.error("Failed loading ore_xp JSON {}", id, e);
                    }
                });
        LOGGER.info("OreXpData loaded {} entries", map.size());
    }
    //Function that maps the Json data.
    private void loadEntry(JsonObject obj, Identifier sourceId) {
        Identifier oreId = new Identifier(obj.get("ore").getAsString());
        Block block = Registries.BLOCK.get(oreId);
        int minXp = obj.get("minXp").getAsInt();
        int maxXp = obj.get("maxXp").getAsInt();
        map.put(block, new IntRange(minXp, maxXp));
    }

    public static IntRange get(Block block) {
        return INSTANCE.map.get(block);
    }

    // Simple min–max range calculator
    public static class IntRange {
        private final int min, max;
        public IntRange(int min, int max) { this.min = min; this.max = max; }
        public int random(Random rand) {
            return min + rand.nextInt(max - min + 1);
        }
    }

    //Registration helper for mod init
    public static void register() {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA)
                .registerReloadListener(INSTANCE);
    }
}