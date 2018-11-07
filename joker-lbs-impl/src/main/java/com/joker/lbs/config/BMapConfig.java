package com.joker.lbs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "bmap")
@PropertySource("classpath:bmap.properties")
public class BMapConfig {
	private Coordconvert coordconvert;

	public static class Coordconvert {
		private String key;
		private String url;
		private String number;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

	}

	public Coordconvert getCoordconvert() {
		return coordconvert;
	}

	public void setCoordconvert(Coordconvert coordconvert) {
		this.coordconvert = coordconvert;
	}

	private Geoconvert geoconvert;

	public static class Geoconvert {
		private String key;
		private String url;
		private String number;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getNumber() {
			return number;
		}

		public void setNumber(String number) {
			this.number = number;
		}

	}

	public Geoconvert getGeoconvert() {
		return geoconvert;
	}

	public void setGeoconvert(Geoconvert geoconvert) {
		this.geoconvert = geoconvert;
	}

}
