package noh.jinil.android.utils.log

import com.google.gson.Gson
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LogFormatTest {

    private lateinit var jsonTestData: JsonTestData

    @BeforeAll
    fun setUp() {
        LogFormat.initialize(debuggable = true)

        jsonTestData = JsonTestData(
            nowSearching = true,
            photos = arrayListOf(
                "HelloWorld.jpg",
                "Jino.gif"
            ),
            videos = arrayListOf(
                "Something.mkv",
                "Someone.mp4"
            )
        )
    }

    @Test
    @DisplayName("HTTP RESPONSE 응답 출력 테스트")
    fun httpResponse() {
        LogFormat.httpResponse(Gson().toJson(arrayOf(jsonTestData, jsonTestData)))
    }

    @Test
    @DisplayName("Simple 스트링 테스트")
    fun simpleTextTest() {
        LogFormat.receive("null")
    }

    @Test
    @DisplayName("Null 데이터 테스트")
    fun nullTextTest() {
        LogFormat.receive(null)
    }
}