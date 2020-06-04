package wooteco.subway;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import wooteco.subway.service.line.dto.LineDetailResponse;
import wooteco.subway.service.line.dto.LineResponse;
import wooteco.subway.service.line.dto.WholeSubwayResponse;
import wooteco.subway.service.member.dto.MemberResponse;
import wooteco.subway.service.member.dto.TokenResponse;
import wooteco.subway.service.path.dto.PathResponse;
import wooteco.subway.service.station.dto.StationResponse;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("/truncate.sql")
public class AcceptanceTest {
    public static final String STATION_NAME_KANGNAM = "강남역";
    public static final String STATION_NAME_YEOKSAM = "역삼역";
    public static final String STATION_NAME_SEOLLEUNG = "선릉역";
    public static final String STATION_NAME_HANTI = "한티역";
    public static final String STATION_NAME_DOGOK = "도곡역";
    public static final String STATION_NAME_MAEBONG = "매봉역";
    public static final String STATION_NAME_YANGJAE = "양재역";

    public static final String LINE_NAME_2 = "2호선";
    public static final String LINE_NAME_3 = "3호선";
    public static final String LINE_NAME_BUNDANG = "분당선";
    public static final String LINE_NAME_SINBUNDANG = "신분당선";

    public static final String TEST_USER_EMAIL = "brown@email.com";
    public static final String TEST_USER_NAME = "브라운";
    public static final String TEST_USER_PASSWORD = "brown";

    public StationResponse kangnamStation;
    public StationResponse yeoksamStation;
    public StationResponse seolleungStation;
    public StationResponse hantiStation;
    public StationResponse dogokStation;
    public StationResponse maebongStation;
    public StationResponse yangjaeStation;

    @LocalServerPort
    public int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    public static RequestSpecification given() {
        return RestAssured.given().log().all();
    }

    // @formatter:off
    public StationResponse createStation(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return given()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/stations")
                .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().as(StationResponse.class);
    }

    // @formatter:off
    public List<StationResponse> getStations() {
        return given().when()
                    .get("/stations")
                .then()
                    .log().all()
                    .extract()
                    .jsonPath().getList(".", StationResponse.class);
    }

    // @formatter:off
    public void deleteStation(Long id) {
        given().when()
            .delete("/stations/" + id)
        .then()
            .log().all();
    }

    // @formatter:off
    public LineResponse createLine(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("startTime", LocalTime.of(5, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", LocalTime.of(23, 30).format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        return given()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/lines")
                .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().as(LineResponse.class);
    }

    // @formatter:off
    public LineDetailResponse getLine(Long id) {
        return given().when()
                    .get("/lines/" + id)
                .then()
                    .log().all()
                    .extract().as(LineDetailResponse.class);
    }

    // @formatter:off
    public void updateLine(Long id, LocalTime startTime, LocalTime endTime) {
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("endTime", endTime.format(DateTimeFormatter.ISO_LOCAL_TIME));
        params.put("intervalTime", "10");

        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .put("/lines/" + id)
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());
    }

    // @formatter:off
    public List<LineResponse> getLines() {
        return given().when()
                    .get("/lines")
                .then()
                    .log().all()
                    .extract()
                    .jsonPath().getList(".", LineResponse.class);
    }

    // @formatter:off
    public void deleteLine(Long id) {
        given().when()
            .delete("/lines/" + id)
        .then()
            .log().all();
    }

    public void addLineStation(Long lineId, Long preStationId, Long stationId) {
        addLineStation(lineId, preStationId, stationId, 10, 10);
    }

    // @formatter:off
    public void addLineStation(Long lineId, Long preStationId, Long stationId, Integer distance, Integer duration) {
        Map<String, String> params = new HashMap<>();
        params.put("preStationId", preStationId == null ? "" : preStationId.toString());
        params.put("stationId", stationId.toString());
        params.put("distance", distance.toString());
        params.put("duration", duration.toString());

        given()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post("/lines/" + lineId + "/stations")
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());
    }

    // @formatter:off
    public void removeLineStation(Long lineId, Long stationId) {
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .delete("/lines/" + lineId + "/stations/" + stationId)
        .then()
            .log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    // @formatter:off
    public WholeSubwayResponse retrieveWholeSubway() {
        return given().when()
                    .get("/lines/detail")
                .then()
                    .log().all()
                    .extract().as(WholeSubwayResponse.class);
    }

    // @formatter:off
    public PathResponse findPath(Long source, Long target, String type) {
        return given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .get("/paths?source=" + source + "&target=" + target + "&type=" + type)
                    .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(PathResponse.class);
    }

    /**
     * 강남 - 역삼 - 선릉
     * |           |
     * |          한티
     * |           |
     * 양재 - 매봉 - 도곡
     */
    public void initStation() {
        // 역 등록
        kangnamStation = createStation(STATION_NAME_KANGNAM);
        yeoksamStation = createStation(STATION_NAME_YEOKSAM);
        seolleungStation = createStation(STATION_NAME_SEOLLEUNG);
        hantiStation = createStation(STATION_NAME_HANTI);
        dogokStation = createStation(STATION_NAME_DOGOK);
        maebongStation = createStation(STATION_NAME_MAEBONG);
        yangjaeStation = createStation(STATION_NAME_YANGJAE);

        // 2호선
        LineResponse lineResponse1 = createLine("2호선");
        addLineStation(lineResponse1.getId(), null, kangnamStation.getId(), 0, 0);
        addLineStation(lineResponse1.getId(), kangnamStation.getId(), yeoksamStation.getId(), 5, 10);
        addLineStation(lineResponse1.getId(), yeoksamStation.getId(), seolleungStation.getId(), 5, 10);

        // 분당선
        LineResponse lineResponse2 = createLine("분당선");
        addLineStation(lineResponse2.getId(), null, seolleungStation.getId(), 0, 0);
        addLineStation(lineResponse2.getId(), seolleungStation.getId(), hantiStation.getId(), 5, 10);
        addLineStation(lineResponse2.getId(), hantiStation.getId(), dogokStation.getId(), 5, 10);

        // 3호선
        LineResponse lineResponse3 = createLine("3호선");
        addLineStation(lineResponse3.getId(), null, dogokStation.getId(), 0, 0);
        addLineStation(lineResponse3.getId(), dogokStation.getId(), maebongStation.getId(), 5, 10);
        addLineStation(lineResponse3.getId(), maebongStation.getId(), yangjaeStation.getId(), 5, 10);

        // 신분당선
        LineResponse lineResponse4 = createLine("신분당선");
        addLineStation(lineResponse4.getId(), null, kangnamStation.getId(), 0, 0);
        addLineStation(lineResponse4.getId(), kangnamStation.getId(), yangjaeStation.getId(), 40, 3);
    }

    // @formatter:off
    public String createMember(String email, String name, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("name", name);
        params.put("password", password);

        return given()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/members")
                .then()
                    .log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .extract().header("Location");
    }

    // @formatter:off
    public MemberResponse getMember(TokenResponse token) {
        return given()
                    .header("Authorization", token.getTokenType()+" "+token.getAccessToken())
                .when()
                    .get("/members")
                .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(MemberResponse.class);
    }

    // @formatter:off
    public void updateMember(MemberResponse memberResponse, TokenResponse tokenResponse) {
        Map<String, String> params = new HashMap<>();
        params.put("name", "NEW_" + TEST_USER_NAME);
        params.put("password", "NEW_" + TEST_USER_PASSWORD);
        String token = tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken();

        given()
            .header("Authorization", token)
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .put("/members/" + memberResponse.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.OK.value());
    }

    // @formatter:off
    public void deleteMember(MemberResponse memberResponse, TokenResponse tokenResponse) {
        String token = tokenResponse.getTokenType() + " " + tokenResponse.getAccessToken();

        given().when()
            .header("Authorization", token)
            .delete("/members/" + memberResponse.getId())
        .then()
            .log().all()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    // @formatter:off
    public TokenResponse login(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        return given()
                    .body(params)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                    .post("/oauth/token")
                .then()
                    .log().all()
                    .statusCode(HttpStatus.OK.value())
                    .extract().as(TokenResponse.class);
    }
}

