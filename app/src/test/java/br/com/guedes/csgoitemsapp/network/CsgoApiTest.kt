package br.com.guedes.csgoitemsapp.network

import br.com.guedes.csgoitemsapp.model.Crate
import br.com.guedes.csgoitemsapp.model.CrateContain
import br.com.guedes.csgoitemsapp.model.Highlight
import br.com.guedes.csgoitemsapp.model.Skin
import br.com.guedes.csgoitemsapp.model.Sticker
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.net.SocketTimeoutException

class CsgoApiTest {

    // 1) getSkins retorna dois itens
    @Test
    fun getSkins_returnsTwoItems() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = listOf(
                Skin("s1", "Skin One", null, null, null, "img1"),
                Skin("s2", "Skin Two", null, null, null, "img2")
            )

            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        val skins = api.getSkins()
        assertEquals(2, skins.size)
        assertEquals("s1", skins[0].id)
        assertEquals("Skin Two", skins[1].name)
    }

    // 2) getSkins retorna uma lista vazia
    @Test
    fun getSkins_emptyList() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = emptyList()
            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        val skins = api.getSkins()
        assertTrue(skins.isEmpty())
    }

    // 3) getStickers retorna um item
    @Test
    fun getStickers_returnsOne() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = emptyList()
            override suspend fun getStickers(): List<Sticker> = listOf(
                Sticker("st1", "Sticker One", null, null, "img")
            )
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        val stickers = api.getStickers()
        assertEquals(1, stickers.size)
        assertEquals("st1", stickers[0].id)
    }

    // 4) getHighlights retorna um item
    @Test
    fun getHighlights_returnsOne() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = emptyList()
            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> = listOf(
                Highlight("h1", "H", null, null, null, null, null, null, null, "img", null)
            )
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        val highlights = api.getHighlights()
        assertEquals(1, highlights.size)
        assertEquals("h1", highlights[0].id)
    }

    // 5) getCrates retorna um item
    @Test
    fun getCrates_returnsOne() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = emptyList()
            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = listOf(
                Crate("c1", "Crate", null, null, null, null, null, null, null, null)
            )
        }

        val crates = api.getCrates()
        assertEquals(1, crates.size)
        assertEquals("c1", crates[0].id)
    }

    // 6) simula erro de rede (IOException) sendo propagado
    @Test
    fun getSkins_ioExceptionPropagated() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> { throw IOException("network") }
            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        try {
            api.getSkins()
            fail("Esperado IOException")
        } catch (e: Exception) {
            assertTrue(e is IOException)
            assertEquals("network", e.message)
        }
    }

    // 7) simula erro de parsing JSON (JsonSyntaxException) sendo propagado
    @Test
    fun getStickers_jsonSyntaxExceptionPropagated() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = emptyList()
            override suspend fun getStickers(): List<Sticker> { throw com.google.gson.JsonSyntaxException("bad json") }
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        try {
            api.getStickers()
            fail("Esperado JsonSyntaxException")
        } catch (e: Exception) {
            assertTrue(e is com.google.gson.JsonSyntaxException)
            assertEquals("bad json", e.message)
        }
    }

    // 8) simula tempo limite (SocketTimeoutException)
    @Test
    fun getHighlights_timeoutExceptionPropagated() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = emptyList()
            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> { throw SocketTimeoutException("timeout") }
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        try {
            api.getHighlights()
            fail("Esperado SocketTimeoutException")
        } catch (e: Exception) {
            assertTrue(e is SocketTimeoutException)
            assertEquals("timeout", e.message)
        }
    }

    // 9) múltiplas chamadas retornam resultados diferentes
    @Test
    fun multipleCalls_returnDifferentResults() = runBlocking {
        var call = 0
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> {
                call++
                return if (call == 1) listOf(Skin("s1","A",null,null,null,"i")) else listOf(Skin("s2","B",null,null,null,"j"))
            }
            override suspend fun getStickers(): List<Sticker> = emptyList()
            override suspend fun getHighlights(): List<Highlight> = emptyList()
            override suspend fun getCrates(): List<Crate> = emptyList()
        }

        val first = api.getSkins()
        val second = api.getSkins()
        assertEquals("s1", first[0].id)
        assertEquals("s2", second[0].id)
    }

    // 10) verifica mapeamento dos campos dos modelos (teste básico de sanidade)
    @Test
    fun verifyModelFields() = runBlocking {
        val api = object : CsgoApi {
            override suspend fun getSkins(): List<Skin> = listOf(Skin("sX","Some", "desc", null, null, "imgX"))
            override suspend fun getStickers(): List<Sticker> = listOf(Sticker("stX","St", "d", null, "imgS"))
            override suspend fun getHighlights(): List<Highlight> = listOf(Highlight("hX","Hname","desc","event","t0","t1","stage","map","mhn","img","vid"))
            override suspend fun getCrates(): List<Crate> = listOf(Crate("cX","Cname","d","type","date", listOf(CrateContain("i","n",null,null,null,"img")), null, null, false, "imgC"))
        }

        val skin = api.getSkins()[0]
        assertEquals("Some", skin.name)
        assertEquals("imgX", skin.image)

        val sticker = api.getStickers()[0]
        assertEquals("St", sticker.name)

        val highlight = api.getHighlights()[0]
        assertEquals("hX", highlight.id)
        assertEquals("vid", highlight.video)

        val crate = api.getCrates()[0]
        assertEquals("Cname", crate.name)
        assertNotNull(crate.contains)
    }
}
