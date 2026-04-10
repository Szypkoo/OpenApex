package lol.apex.feature.waypoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Waypoint {
    private String name;
    private int x, y, z;
    private boolean visible;
}