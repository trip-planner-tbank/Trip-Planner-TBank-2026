package com.tripplanner.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class GeocodingServiceTest {

    @Test
    void geocodesAddressWithRequiredUserAgentAndCachesResult() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        GeocodingService service = new GeocodingService(
                builder, "https://nominatim.test", "TripPlanner-Test/1.0");

        server.expect(request -> {
                    assertThat(request.getURI().getPath()).isEqualTo("/search");
                    assertThat(request.getURI().getRawQuery())
                            .contains("q=Rudaki%2010")
                            .contains("format=jsonv2")
                            .contains("limit=1");
                    assertThat(request.getHeaders().getFirst(HttpHeaders.USER_AGENT))
                            .isEqualTo("TripPlanner-Test/1.0");
                })
                .andRespond(withSuccess("""
                        [{
                          "display_name": "Rudaki Avenue, Dushanbe, Tajikistan",
                          "lat": "38.5737",
                          "lon": "68.7738"
                        }]
                        """, MediaType.APPLICATION_JSON));

        var first = service.geocode("Rudaki 10");
        var cached = service.geocode("  RUDAKI   10 ");

        assertThat(first.latitude()).isEqualTo(38.5737);
        assertThat(first.longitude()).isEqualTo(68.7738);
        assertThat(cached).isEqualTo(first);
        server.verify();
    }
}
