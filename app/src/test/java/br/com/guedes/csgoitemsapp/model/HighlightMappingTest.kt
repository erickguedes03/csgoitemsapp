package br.com.guedes.csgoitemsapp.model

import org.junit.Test
import org.junit.Assert.*

class HighlightMappingTest {
    @Test
    fun highlightMapsToItem() {
        val h = Highlight(
            id = "h1",
            name = "Test Highlight",
            description = "desc",
            tournament_event = "Event",
            team0 = "A",
            team1 = "B",
            stage = "Stage",
            map = "de_mirage",
            market_hash_name = "mhn",
            image = "https://example.com/img.png",
            video = "https://example.com/video.webm"
        )

        val item = Item(
            id = h.id,
            name = h.name,
            description = h.description,
            image = h.image,
            subtext = h.tournament_event ?: "",
            rarityColor = null,
            videoUrl = h.video,
            extraSummary = "${h.team0} vs ${h.team1} • ${h.map}"
        )

        assertEquals(h.id, item.id)
        assertEquals(h.name, item.name)
        assertEquals(h.image, item.image)
        assertEquals(h.video, item.videoUrl)
        assertTrue(item.extraSummary!!.contains("A vs B"))
    }
}

