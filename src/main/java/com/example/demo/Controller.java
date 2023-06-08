package com.example.demo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchTemplateResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;


@RestController
public class Controller {

    @CrossOrigin
    @GetMapping("/indexmovies/title/{movie_title}")
    private ResponseEntity<String> index_movie(@PathVariable("movie_title") String movieTitle) {

        final String uri = String.format("https://jsonmock.hackerrank.com/api/moviesdata/search/?Title=%s&page=1",movieTitle);

        RestTemplate restTemplate = new RestTemplate();
        String restResult = restTemplate.getForObject(uri, String.class);
        JSONObject root = new JSONObject(restResult);

        JSONArray data = root.getJSONArray("data");
        int totalPages = root.getInt("total_pages");
        int page = root.getInt("page");

        System.out.println(movieTitle);

        ArrayList<Movie> myMovies = new ArrayList<>();

        data.forEach(element -> {
            myMovies.add(new Movie(
                    (Integer) ((JSONObject) element).get("Year"),
                    (String) ((JSONObject) element).get("imdbID"),
                    (String) ((JSONObject) element).get("Title")));
        });
        while (page < totalPages) {
            page++;
            String uriNextPage = String.format("https://jsonmock.hackerrank.com/api/moviesdata/search/?Title=%s&page=%s",movieTitle, page);
            JSONArray newData = getData(uriNextPage);
            newData.forEach(element -> {
                myMovies.add(new Movie(
                        (Integer) ((JSONObject) element).get("Year"),
                        (String) ((JSONObject) element).get("imdbID"),
                        (String) ((JSONObject) element).get("Title")));
            });

        }

        // index documents
        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,new JacksonJsonpMapper());
        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);

        BulkRequest.Builder br = new BulkRequest.Builder();

        for (Movie movie : myMovies) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index("movies")
                            .id(movie.getImdbID())
                            .document(movie)
                    )
            );
        }

        try {

            BulkResponse result = client.bulk(br.build());

            if (result.errors()) {
                System.out.println("Bulk had errors");
                for (BulkResponseItem item: result.items()) {
                    if (item.error() != null) {
                        System.out.println(item.error().reason());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("error" +  e.getMessage());
        }


        return new ResponseEntity<String>("Index Created",HttpStatus.OK);


    }

    // EJEMPLO LLAMADA LOCAL http://localhost:8080/searchmovie/title/The
    @CrossOrigin
    @GetMapping("/searchmovie/title/{movie_title}")
    private ResponseEntity<List<Movie>>search_movie(@PathVariable("movie_title") String movieTitle) {

        // index documents
        // Create the low-level client
        RestClient restClient = RestClient.builder(
                new HttpHost("localhost", 9200)).build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient,new JacksonJsonpMapper());
        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);

        try{
            client.putScript(r -> r
                    .id("searchByTitle")
                    .script(s -> s
                            .lang("mustache")
                            .source("{\"size\":1000,\"query\":{\"match\":{\"{{field}}\":\"{{value}}\"}}}")
                    ));

            SearchTemplateResponse<JsonData> searchResponse = client.searchTemplate(r -> r
                            .index("movies")
                            .id("searchByTitle")
                            .params("field", JsonData.of("title"))
                            .params("value", JsonData.of(movieTitle)),
                    JsonData.class
            );

            List<Movie> moviesReturn = new ArrayList<>();
            for (Hit<JsonData> hit: searchResponse.hits().hits()) {
                JSONObject movieJson = new JSONObject(hit.source().toString());
                new JSONObject(hit.source().toString()).get("title");
                moviesReturn.add(new Movie(Integer.parseInt(movieJson.get("year").toString()),
                        (movieJson.get("imdbID").toString()),
                        (movieJson.get("title").toString())));

            }
            return new ResponseEntity<List<Movie>>(moviesReturn, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("error" +  e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private JSONArray getData(String uri) {

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);
        JSONObject root = new JSONObject(result);

        return root.getJSONArray("data");

    }

    ;

    public class Movie {
        Integer Year;
        String imdbID;
        String Title;

        public Movie(Integer year, String imdbID, String title) {
            Year = year;
            this.imdbID = imdbID;
            Title = title;
        }

        public Integer getYear() {
            return Year;
        }

        public void setYear(Integer year) {
            Year = year;
        }

        public String getImdbID() {
            return imdbID;
        }

        public void setImdbID(String imdbID) {
            this.imdbID = imdbID;
        }

        public String getTitle() {
            return Title;
        }

        public void setTitle(String title) {
            Title = title;
        }
    }

}
