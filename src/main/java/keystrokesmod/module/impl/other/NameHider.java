package keystrokesmod.module.impl.other;

import com.google.gson.JsonObject;
// import com.mojang.authlib.com.mojang.authlib.GameProfile; // unused
import keystrokesmod.module.Module;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.TextSetting;
import keystrokesmod.utility.TextUtils;
import keystrokesmod.utility.Utils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class NameHider extends Module {
    private static final long PROFILE_CACHE_REFRESH_MS = 250L;
    private static final int MIN_ALIAS_NUMBER = 1;
    private static final String DEFAULT_FAKE_NAME = "You";
    private static final String DEFAULT_HIDE_ALL_PREFIX = "Player";

    public static String fakeName = DEFAULT_FAKE_NAME;
    public static ButtonSetting hideAllNames;
    public static TextSetting fakeNameSetting;
    public static TextSetting hideAllPrefixSetting;

    private static final LinkedHashSet<String> detectedSelfNames = new LinkedHashSet<String>();

    private static String formattedFakeName = DEFAULT_FAKE_NAME;
    private static String formattedHideAllPrefix = DEFAULT_HIDE_ALL_PREFIX;
    private static String cachedSelfKey;
    private static boolean cachedHideAllState;
    private static List<String> cachedSelfNames = Collections.emptyList();
    private static Map<String, List<String>> cachedVisibleNamesByKey = Collections.emptyMap();
    private static List<Map.Entry<String, String>> cachedOtherNameReplacements = Collections.emptyList();
    private static Map<String, Integer> cachedAliasNumbers = Collections.emptyMap();
    private static long lastProfileCacheRefresh;
    private static int transformVersion;
    private static int lastTransformVersion = -1;
    private static String lastInput;
    private static String lastOutput;

    public NameHider() {
        super("Name Hider", Module.category.other);
        this.registerSetting(fakeNameSetting = new TextSetting("Fake name", fakeName, 48) {
            @Override
            public void setText(String text) {
                super.setText(normalizeFakeName(text));
                applyFakeName(getText());
            }

            @Override
            public void loadProfile(JsonObject data) {
                if (data == null) {
                    return;
                }

                String profileKey = getProfileKey();
                if (data.has(profileKey) && data.get(profileKey).isJsonPrimitive()) {
                    setText(data.getAsJsonPrimitive(profileKey).getAsString());
                }
            }
        });
        this.registerSetting(hideAllNames = new ButtonSetting("Hide all names", false));
        this.registerSetting(hideAllPrefixSetting = new TextSetting("Hide-all prefix", DEFAULT_HIDE_ALL_PREFIX, 24) {
            @Override
            public void setText(String text) {
                super.setText(normalizeHideAllPrefix(text));
                applyHideAllPrefix(getText());
            }

            @Override
            public void loadProfile(JsonObject data) {
                if (data == null) {
                    return;
                }

                String profileKey = getProfileKey();
                if (data.has(profileKey) && data.get(profileKey).isJsonPrimitive()) {
                    setText(data.getAsJsonPrimitive(profileKey).getAsString());
                }
            }
        });
        hideAllPrefixSetting.visible = false;
    }

    public static String getFakeName(String input) {
        if (!shouldProcessText(input)) {
            return input;
        }

        refreshCaches();

        if (lastTransformVersion == transformVersion && input.equals(lastInput)) {
            return lastOutput;
        }

        String replaced = replaceNames(input, cachedSelfNames, formattedFakeName);
        if (cachedHideAllState) {
            for (Map.Entry<String, String> entry : cachedOtherNameReplacements) {
                replaced = TextUtils.replaceKeepingFormatting(replaced, entry.getKey(), entry.getValue());
            }
        }

        lastInput = input;
        lastOutput = replaced;
        lastTransformVersion = transformVersion;
        return replaced;
    }

    public static Text getPlayerDisplayName(PlayerEntity player, Text original) {
        if (original == null || !shouldProcessText(original.getString())) {
            return original;
        }

        refreshCaches();
        String originalText = String.valueOf(original.getString());
        String replaced = getDisplayTextForPlayer(player, originalText);
        if (Objects.equals(originalText, replaced)) {
            return original;
        }

        Object component = Text.literal(replaced);
        // component.setStyle disabled
        return Text.literal(component.toString());
    }

    public static String getTabName(PlayerListEntry playerInfo, String original) {
        if (!shouldProcessText(original)) {
            return original;
        }

        refreshCaches();
        String key = getIdentityKey(playerInfo);
        if (key == null) {
            return original;
        }

        if (isSelfKey(key)) {
            return replaceNames(original, getTabVisibleNames(playerInfo, key), formattedFakeName);
        }

        if (!cachedHideAllState) {
            return original;
        }

        String replacement = getFormattedAlias(key);
        return replacement == null ? original : replaceNames(original, getTabVisibleNames(playerInfo, key), replacement);
    }

    public static void setFakeName(String name) {
        String normalized = normalizeFakeName(name);
        if (fakeNameSetting != null && !normalized.equals(fakeNameSetting.getText())) {
            fakeNameSetting.setText(normalized);
            return;
        }
        applyFakeName(normalized);
    }

    private static String getDisplayTextForPlayer(PlayerEntity player, String original) {
        if (player == null) {
            return original;
        }

        String key = getIdentityKey(player);
        if (isSelfPlayer(player, key)) {
            return replaceNames(original, getEntityVisibleNames(player, key), formattedFakeName);
        }

        if (!cachedHideAllState) {
            return original;
        }

        String replacement = getFormattedAlias(key);
        return replacement == null ? original : replaceNames(original, getEntityVisibleNames(player, key), replacement);
    }

    private static void refreshCaches() {
        if (mc.player == null) {
            return;
        }

        boolean hideEveryone = hideAllNames != null && hideAllNames.isToggled();
        if (cachedHideAllState != hideEveryone) {
            cachedHideAllState = hideEveryone;
            clearHideAllCaches();
            markCacheDirty();
        }

        String selfKey = getSelfKey();
        if (!Objects.equals(cachedSelfKey, selfKey)) {
            cachedSelfKey = selfKey;
            markCacheDirty();
        }

        List<String> selfNames = buildSelfNames();
        if (!selfNames.equals(cachedSelfNames)) {
            cachedSelfNames = selfNames;
            markCacheDirty();
        }

        if (!hideEveryone) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastProfileCacheRefresh < PROFILE_CACHE_REFRESH_MS) {
            return;
        }

        lastProfileCacheRefresh = now;
        HideAllCache cache = buildHideAllCache(selfNames);
        if (!cache.visibleNamesByKey.equals(cachedVisibleNamesByKey)
            || !cache.aliasNumbers.equals(cachedAliasNumbers)
            || !cache.replacements.equals(cachedOtherNameReplacements)) {
            cachedVisibleNamesByKey = cache.visibleNamesByKey;
            cachedAliasNumbers = cache.aliasNumbers;
            cachedOtherNameReplacements = cache.replacements;
            markCacheDirty();
        }
    }

    private static HideAllCache buildHideAllCache(List<String> selfNames) {
        LinkedHashMap<String, LinkedHashSet<String>> targets = new LinkedHashMap<String, LinkedHashSet<String>>();
        HashSet<String> protectedNames = new HashSet<String>();
        for (String selfName : selfNames) {
            protectedNames.add(normalizeName(selfName));
        }

        collectTargetsFromTab(targets, protectedNames);
        collectTargetsFromWorld(targets, protectedNames);

        if (targets.isEmpty()) {
            return HideAllCache.EMPTY;
        }

        LinkedHashMap<String, List<String>> visibleNamesByKey = new LinkedHashMap<String, List<String>>();
        for (Map.Entry<String, LinkedHashSet<String>> entry : targets.entrySet()) {
            visibleNamesByKey.put(entry.getKey(), sortNames(entry.getValue()));
        }

        LinkedHashMap<String, Integer> aliasNumbers = assignAliasNumbers(visibleNamesByKey.keySet());
        List<Map.Entry<String, String>> replacements = new ArrayList<Map.Entry<String, String>>();
        for (Map.Entry<String, List<String>> entry : visibleNamesByKey.entrySet()) {
            Integer aliasNumber = aliasNumbers.get(entry.getKey());
            if (aliasNumber == null) {
                continue;
            }

            String replacement = formattedHideAllPrefix + aliasNumber;
            for (String visibleName : entry.getValue()) {
                replacements.add(new java.util.AbstractMap.SimpleEntry<String, String>(visibleName, replacement));
            }
        }

        replacements.sort(new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> first, Map.Entry<String, String> second) {
                int lengthCompare = Integer.compare(second.getKey().length(), first.getKey().length());
                return lengthCompare != 0 ? lengthCompare : first.getKey().compareToIgnoreCase(second.getKey());
            }
        });

        return new HideAllCache(visibleNamesByKey, aliasNumbers, replacements);
    }

    private static void collectTargetsFromTab(Map<String, LinkedHashSet<String>> targets, Set<String> protectedNames) {
        if (mc.getNetworkHandler() == null || mc.getNetworkHandler().getPlayerList() == null) {
            return;
        }

        PlayerListEntry self = getSelfPlayerInfo();
        for (PlayerListEntry playerInfo : mc.getNetworkHandler().getPlayerList()) {
            if (playerInfo == null || playerInfo == self) {
                continue;
            }

            addTargetName(targets, protectedNames, getIdentityKey(playerInfo), getProfileName(playerInfo));
        }
    }

    private static void collectTargetsFromWorld(Map<String, LinkedHashSet<String>> targets, Set<String> protectedNames) {
        if (mc.world == null || mc.world.getPlayers() == null) {
            return;
        }

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == null || player == mc.player) {
                continue;
            }

            PlayerListEntry playerInfo = getPlayerInfo(player);
            String key = playerInfo != null ? getIdentityKey(playerInfo) : getIdentityKey(player.getUuid(), player.getName());
            addTargetName(targets, protectedNames, key, player.getName());
            if (playerInfo != null) {
                addTargetName(targets, protectedNames, key, getProfileName(playerInfo));
            }
        }
    }

    private static void addTargetName(Map<String, LinkedHashSet<String>> targets, Set<String> protectedNames, String key, String candidate) {
        String normalized = sanitizeName(candidate);
        if (key == null || normalized.isEmpty() || protectedNames.contains(normalizeName(normalized))) {
            return;
        }

        LinkedHashSet<String> names = targets.get(key);
        if (names == null) {
            names = new LinkedHashSet<String>();
            targets.put(key, names);
        }
        names.add(normalized);
    }

    private static LinkedHashMap<String, Integer> assignAliasNumbers(Set<String> keys) {
        LinkedHashMap<String, Integer> aliasNumbers = new LinkedHashMap<String, Integer>();
        HashSet<Integer> usedAliasNumbers = new HashSet<Integer>();

        for (String key : keys) {
            Integer existing = cachedAliasNumbers.get(key);
            if (existing != null && usedAliasNumbers.add(existing)) {
                aliasNumbers.put(key, existing);
            }
        }

        for (String key : keys) {
            if (aliasNumbers.containsKey(key)) {
                continue;
            }

            int aliasNumber = MIN_ALIAS_NUMBER;
            while (usedAliasNumbers.contains(aliasNumber)) {
                aliasNumber++;
            }
            aliasNumbers.put(key, aliasNumber);
            usedAliasNumbers.add(aliasNumber);
        }

        return aliasNumbers;
    }

    private static List<String> buildSelfNames() {
        rememberSelfName(mc.player.getName());

        PlayerListEntry selfPlayerInfo = getSelfPlayerInfo();
        if (selfPlayerInfo != null) {
            rememberSelfName(getProfileName(selfPlayerInfo));
        }

        return sortNames(detectedSelfNames);
    }

    private static boolean rememberSelfName(String candidate) {
        String normalized = sanitizeName(candidate);
        return !normalized.isEmpty() && detectedSelfNames.add(normalized);
    }

    private static List<String> getEntityVisibleNames(PlayerEntity player, String key) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        addNames(names, cachedVisibleNamesByKey.get(key));
        addName(names, player.getName());

        PlayerListEntry playerInfo = getPlayerInfo(player);
        if (playerInfo != null) {
            addName(names, getProfileName(playerInfo));
        }

        if (isSelfPlayer(player, key)) {
            addNames(names, cachedSelfNames);
        }

        return sortNames(names);
    }

    private static List<String> getTabVisibleNames(PlayerListEntry playerInfo, String key) {
        LinkedHashSet<String> names = new LinkedHashSet<String>();
        addNames(names, cachedVisibleNamesByKey.get(key));
        addName(names, getProfileName(playerInfo));

        if (isSelfKey(key)) {
            addNames(names, cachedSelfNames);
        }

        return sortNames(names);
    }

    private static void addNames(Set<String> target, Iterable<String> names) {
        if (names == null) {
            return;
        }

        for (String name : names) {
            addName(target, name);
        }
    }

    private static void addName(Set<String> target, String name) {
        String normalized = sanitizeName(name);
        if (!normalized.isEmpty()) {
            target.add(normalized);
        }
    }

    private static List<String> sortNames(Iterable<String> names) {
        ArrayList<String> sorted = new ArrayList<String>();
        if (names != null) {
            for (String name : names) {
                String normalized = sanitizeName(name);
                if (!normalized.isEmpty() && !sorted.contains(normalized)) {
                    sorted.add(normalized);
                }
            }
        }
        Collections.sort(sorted, new Comparator<String>() {
            @Override
            public int compare(String first, String second) {
                int lengthCompare = Integer.compare(second.length(), first.length());
                return lengthCompare != 0 ? lengthCompare : first.compareToIgnoreCase(second);
            }
        });
        return sorted;
    }

    private static String replaceNames(String input, Iterable<String> names, String replacement) {
        String replaced = String.valueOf(TextUtils.replaceAllKeepingFormatting(input, names, replacement));
        return replaced == null ? input : replaced;
    }

    private static PlayerListEntry getSelfPlayerInfo() {
        return mc.getNetworkHandler() == null || mc.player == null ? null : mc.getNetworkHandler().getPlayerList().stream().filter(e -> e.getProfile().getId().equals(mc.player.getUuid())).findFirst().orElse(null);
    }

    private static PlayerListEntry getPlayerInfo(PlayerEntity player) {
        return mc.getNetworkHandler() == null || player == null ? null : mc.getNetworkHandler().getPlayerList().stream().filter(e -> e.getProfile().getId().equals(player.getUuid())).findFirst().orElse(null);
    }

    private static String getSelfKey() {
        PlayerListEntry selfPlayerInfo = getSelfPlayerInfo();
        if (selfPlayerInfo != null) {
            return getIdentityKey(selfPlayerInfo);
        }
        return mc.player == null ? null : getIdentityKey(mc.player.getUuid(), mc.player.getName());
    }

    private static boolean isSelfPlayer(PlayerEntity player, String key) {
        return player == mc.player || isSelfKey(key);
    }

    private static boolean isSelfKey(String key) {
        return key != null && key.equals(cachedSelfKey);
    }

    private static String getFormattedAlias(String key) {
        Integer aliasNumber = cachedAliasNumbers.get(key);
        return aliasNumber == null ? null : formattedHideAllPrefix + aliasNumber;
    }

    private static String getProfileName(PlayerListEntry playerInfo) {
        com.mojang.authlib.GameProfile profile = playerInfo == null ? null : playerInfo.getProfile();
        return profile == null ? "" : profile.getName();
    }

    private static String getIdentityKey(PlayerListEntry playerInfo) {
        com.mojang.authlib.GameProfile profile = playerInfo == null ? null : playerInfo.getProfile();
        return profile == null ? null : getIdentityKey(profile.getId(), profile.getName());
    }

    private static String getIdentityKey(PlayerEntity player) {
        PlayerListEntry playerInfo = getPlayerInfo(player);
        if (playerInfo != null) {
            return getIdentityKey(playerInfo);
        }
        return player == null ? null : getIdentityKey(player.getUuid(), player.getName());
    }

    private static String getIdentityKey(UUID uniqueId, String fallbackName) {
        if (uniqueId != null) {
            return uniqueId.toString();
        }

        String normalized = normalizeName(fallbackName);
        return normalized.isEmpty() ? null : normalized;
    }

    private static String normalizeFakeName(String name) {
        String normalized = name == null ? "" : name.trim();
        return normalized.isEmpty() ? DEFAULT_FAKE_NAME : normalized;
    }

    private static String normalizeHideAllPrefix(String value) {
        String normalized = value == null ? "" : value.trim();
        return normalized.isEmpty() ? DEFAULT_HIDE_ALL_PREFIX : normalized;
    }

    private static String sanitizeName(String value) {
        return Utils.stripColor(value == null ? "" : value).trim();
    }

    private static String normalizeName(String value) {
        return sanitizeName(value).toLowerCase(Locale.ROOT);
    }

    private static boolean shouldProcessText(String input) {
        return input != null && !input.isEmpty() && mc.player != null;
    }

    private static void applyFakeName(String name) {
        String normalized = normalizeFakeName(name);
        String nextFormattedFakeName = Utils.formatColor(normalized);
        if (Objects.equals(fakeName, normalized) && Objects.equals(formattedFakeName, nextFormattedFakeName)) {
            return;
        }

        fakeName = normalized;
        formattedFakeName = nextFormattedFakeName;
        markCacheDirty();
    }

    private static void applyHideAllPrefix(String value) {
        String normalized = normalizeHideAllPrefix(value);
        String nextFormattedHideAllPrefix = Utils.formatColor(normalized);
        if (Objects.equals(formattedHideAllPrefix, nextFormattedHideAllPrefix)) {
            return;
        }

        formattedHideAllPrefix = nextFormattedHideAllPrefix;
        clearHideAllCaches();
        markCacheDirty();
    }

    private static void clearHideAllCaches() {
        cachedVisibleNamesByKey = Collections.emptyMap();
        cachedOtherNameReplacements = Collections.emptyList();
        cachedAliasNumbers = Collections.emptyMap();
        lastProfileCacheRefresh = 0L;
    }

    private static void markCacheDirty() {
        transformVersion++;
        lastTransformVersion = -1;
        lastInput = null;
        lastOutput = null;
        lastProfileCacheRefresh = 0L;
    }

    @Override
    public void guiUpdate() {
        hideAllPrefixSetting.setVisible(hideAllNames.isToggled(), this);
    }

    private static final class HideAllCache {
        private static final HideAllCache EMPTY = new HideAllCache(Collections.<String, List<String>>emptyMap(), Collections.<String, Integer>emptyMap(), Collections.<Map.Entry<String, String>>emptyList());

        private final Map<String, List<String>> visibleNamesByKey;
        private final Map<String, Integer> aliasNumbers;
        private final List<Map.Entry<String, String>> replacements;

        private HideAllCache(Map<String, List<String>> visibleNamesByKey, Map<String, Integer> aliasNumbers, List<Map.Entry<String, String>> replacements) {
            this.visibleNamesByKey = visibleNamesByKey;
            this.aliasNumbers = aliasNumbers;
            this.replacements = replacements;
        }
    }
}
