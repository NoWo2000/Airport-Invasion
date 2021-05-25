package aas.unit;

import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Test;

import aas.controller.export.GeoJsonExport;
import aas.model.util.Point;

public class GeoJsonExportTest {

	@Test
	public void testGeoJsonExport() {
		GeoJsonExport export = new GeoJsonExport("test.geojson");
		export.addPostion("test1", 0, new Point(0.0, 0.0));
		export.addPostion("test1", 1, new Point(1.0, 1.0));
		export.addPostion("test1", 2, new Point(2.0, 1.0));
		export.addPostion("test1", 3, new Point(3.0, 1.0));
		export.finish();
		
		assert(new File("test.geojson").exists());
	}

	@Test
	public void testAddPostion() {
		fail("Not yet implemented");
	}

	@Test
	public void testFinish() {
		fail("Not yet implemented");
	}

}
