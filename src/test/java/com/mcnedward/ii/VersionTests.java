package com.mcnedward.ii;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.mcnedward.ii.utils.VersionUtils;

/**
 * @author Edward - Jul 14, 2016
 *
 */
public class VersionTests {
	
	@Test
	public void versionUtils_findsCorrectForBasicVersion() {
		String version = "1.2.3";
		String path = "C:/QC/systems/project-" + version;
		
		String expectedVersion = VersionUtils.findVersion(path);
		
		assertThat(expectedVersion, is(version));
	}
	
	@Test
	public void versionUtils_findsCorrectForVersionWithExtra() {
		String version = "1.2.3";
		String path = "C:/QC/systems/project-" + version + "-extra4.6";
		
		String expectedVersion = VersionUtils.findVersion(path);
		
		assertThat(expectedVersion, is(version));
	}
	
	@Test
	public void versionUtils_findsCorrectForVersionWithMoreThan3() {
		String version = "1.2.3.4.5";
		String path = "C:/QC/systems/project-" + version;
		
		String expectedVersion = VersionUtils.findVersion(path);
		
		assertThat(expectedVersion, is(version));
	}

	@Test
	public void versionUtils_findsCorrectForVersionsWithHighNumbers() {
		String version = "1.234234.34234342";
		String path = "C:/QC/systems/project-" + version;
		
		String expectedVersion = VersionUtils.findVersion(path);
		
		assertThat(expectedVersion, is(version));
	}
}
