package com.example.getirme.component;

import de.topobyte.osm4j.core.access.OsmIterator;
import de.topobyte.osm4j.core.model.iface.EntityType;
import de.topobyte.osm4j.core.model.iface.OsmNode;      // ← düzeltildi
import de.topobyte.osm4j.core.model.iface.OsmWay;       // ← düzeltildi
import de.topobyte.osm4j.pbf.seq.PbfIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;

@Component
public class GraphLoader {

    @Getter
    private final SimpleWeightedGraph<Long, DefaultWeightedEdge> graph =
            new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

    /** nodeId → [lat, lon] */
    public final Long2ObjectMap<double[]> coords = new Long2ObjectOpenHashMap<>();

    @PostConstruct
    void load() throws IOException {
        // 1) node’ları oku
        try (FileInputStream in = new FileInputStream("data/roads.pbf")) {
            OsmIterator it = new PbfIterator(in, false);
            it.forEachRemaining(ec -> {
                if (ec.getType() != EntityType.Node) return;
                OsmNode n = (OsmNode) ec.getEntity();          // ← cast düzeltildi
                coords.put(n.getId(), new double[]{n.getLatitude(), n.getLongitude()});
                graph.addVertex(n.getId());
            });
        }

        // 2) way → edge
        // 2) way → edge
        try (FileInputStream in = new FileInputStream("data/roads.pbf")) {
            OsmIterator it = new PbfIterator(in, false);

            it.forEachRemaining(ec -> {
                if (ec.getType() != EntityType.Way) return;
                OsmWay w = (OsmWay) ec.getEntity();

                /* ---- Tag’leri tara ---- */
                boolean isHighway = false;
                boolean oneway    = false;

                for (int i = 0; i < w.getNumberOfTags(); i++) {
                    var tag = w.getTag(i);          // OsmTag
                    switch (tag.getKey()) {
                        case "highway" -> isHighway = true;
                        case "oneway"  -> oneway    = tag.getValue().equals("yes");
                    }
                }
                if (!isHighway) return;             // otoyol değil → pas geç

                /* ---- Node ID’lerini topla ---- */
                int n = w.getNumberOfNodes();
                for (int i = 0; i < n - 1; i++) {
                    long a = w.getNodeId(i);
                    long b = w.getNodeId(i + 1);
                    if (!coords.containsKey(a) || !coords.containsKey(b)) continue;

                    addEdge(a, b);
                    if (!oneway) addEdge(b, a);
                }
            });
        }


        System.out.printf("Graph loaded: %,d nodes / %,d edges%n",
                graph.vertexSet().size(), graph.edgeSet().size());
    }

    private void addEdge(long from, long to) {
        DefaultWeightedEdge e = graph.addEdge(from, to);
        if (e == null) return;
        double[] c1 = coords.get(from), c2 = coords.get(to);
        graph.setEdgeWeight(e, haversine(c1[0], c1[1], c2[0], c2[1]));
    }

    public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6_371_000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }

    /** lat/lon’a en yakın düğüm (MVP: lineer tarama) */
    public long nearest(double lat, double lon) {
        return coords.long2ObjectEntrySet().stream()
                .min(Comparator.comparingDouble(e ->
                        haversine(lat, lon, e.getValue()[0], e.getValue()[1])))
                .map(Long2ObjectMap.Entry::getLongKey)
                .orElseThrow();
    }
}
