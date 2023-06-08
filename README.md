# springboot_elasticsearchAPI
API with springboot which saves into a elasticsearch DB

Levantar ambiente docker con imagen de ElasticSearch:
docker run \

      --name elasticsearch \

      --net elastic \

      -p 9200:9200 \

      -e discovery.type=single-node \

      -e ES_JAVA_OPTS="-Xms1g -Xmx1g"\

      -e xpack.security.enabled=false \

      -it \

      docker.elastic.co/elasticsearch/elasticsearch:8.2.2


Existen 2 endpoints a utilizar:

http://localhost:8080/indexmovies/title/{{nombreDelTitulo}}

El endpoint indexa los resultados obtenidos en ElasticSearch usando la variable {{nombreDelTitulo}} para buscar en la base de datos Marsie. El usado para las pruebas ha sido "the"

http://localhost:8080/searchmovie/title/{{nombreDelTitulo}}

El endpoint busca en la base de ElasticSearch por titulo
