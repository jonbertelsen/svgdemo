package FunctionLayer;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;

public class SvgTest {

    Svg svg;

    @Before
    public void setUp() throws Exception {
        svg = new Svg(800, 600, "0,0,800,600", 0,0);
    }

    @Test
    public void addRect01() {
        svg.addRect(0,0,100,100);
        String expectedRect = "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" height=\"600\" width=\"800\" viewBox=\"0,0,800,600\" preserveAspectRatio=\"xMinYMin\"><rect x=\"0\" y=\"0\" height=\"100\" width=\"100\" style=\"stroke:#000000; fill: #ffffff\" /></svg>";
        assertEquals(svg.toString(),expectedRect);
    }

    @Test
    public void addRect02() {
        svg.addRect(0,0,99,99);
        String expectedRect = "<rect x=\"0\" y=\"0\" height=\"99\" width=\"99\" style=\"stroke:#000000; fill: #ffffff\" />";
        assertThat(svg.toString(), containsString(expectedRect));
    }
}

