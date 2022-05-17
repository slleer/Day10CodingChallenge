import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Author: Stephen Leer
 */

public class SongCacheImpl implements SongCache{
    private final ConcurrentMap<String, Integer> songMap;

    public SongCacheImpl() {
        this.songMap = new ConcurrentHashMap<>();
    }

    @Override
    public void recordSongPlays(String songId, int numPlays) {
        Optional<Integer> idExists = Optional.ofNullable(this.songMap.putIfAbsent(songId, numPlays));
        if(idExists.isPresent()){
            this.songMap.computeIfPresent(songId, (k, v) -> v+numPlays);
            //this.songMap.replace(songId, this.songMap.get(songId), (this.songMap.get(songId) + numPlays))
        }
    }

    @Override
    public int getPlaysForSong(String songId) {
        if(!this.songMap.containsKey(songId))
            return -1;
        return this.songMap.get(songId);
    }

    @Override
    public List<String> getTopNSongsPlayed(int n) {
        if(this.songMap.isEmpty())
            return Collections.emptyList();
        return this.songMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(n)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    // Unfamiliar working with unit tests,
    // wasn't able to use the exact unit test from the Challenge but this works.
    @Test
    public void cacheIsWorking() {
        SongCache cache = new SongCacheImpl();
        cache.recordSongPlays("ID-1", 3);
        cache.recordSongPlays("ID-1", 1);
        cache.recordSongPlays("ID-2", 2);
        cache.recordSongPlays("ID-3", 5);
        Assert.assertEquals(cache.getPlaysForSong("ID-1"), 4);
        Assert.assertEquals(cache.getPlaysForSong("ID-9"), -1);

        Assert.assertEquals(cache.getTopNSongsPlayed(2), Arrays.asList("ID-3", "ID-1"));
        Assert.assertEquals(cache.getTopNSongsPlayed(0), Collections.emptyList());
    }
}

