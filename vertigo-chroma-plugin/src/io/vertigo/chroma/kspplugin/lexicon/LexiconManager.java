package io.vertigo.chroma.kspplugin.lexicon;

import io.vertigo.chroma.kspplugin.Activator;
import io.vertigo.chroma.kspplugin.utils.ErrorUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manager des lexiques.
 */
public final class LexiconManager {

	private static LexiconManager instance;
	private final LexiconMap map = new LexiconMap();

	private LexiconManager() {
		// RAS.
	}

	/**
	 * @return Instance du singleton.
	 */
	public static synchronized LexiconManager getInstance() {
		if (instance == null) {
			instance = new LexiconManager();
		}
		return instance;
	}

	/**
	 * Renvoie la liste de mots d'un lexique donné.
	 * 
	 * @param lexicon Lexique.
	 * @return Liste des mots.
	 */
	public String[] getWords(Lexicons lexicon) {
		if (!map.containsKey(lexicon)) {
			map.put(lexicon, getWordsCore(lexicon));
		}
		return map.getOrDefault(lexicon, new String[0]);
	}

	private static String[] getWordsCore(Lexicons lexicon) {
		List<String> list = new ArrayList<>();
		URL url = getUrl(lexicon);
		if (url == null) {
			return list.toArray(new String[0]);
		}
		try (
		/* InputStream */
		InputStream inputStream = url.openConnection().getInputStream();
		/* Reader */
		BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf8")))) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				String trim = inputLine.trim();
				if (!trim.isEmpty()) {
					list.add(trim);
				}
			}
		} catch (IOException e) {
			ErrorUtils.handle(e);
		}
		return list.toArray(new String[0]);
	}

	private static URL getUrl(Lexicons lexicon) {
		try {
			return new URL(String.format("platform:/plugin/%s/%s", Activator.PLUGIN_ID, lexicon.getPath()));
		} catch (IOException e) {
			ErrorUtils.handle(e);
		}
		return null;
	}

	/**
	 * Map lexique vers liste de mots.
	 */
	private static class LexiconMap extends ConcurrentHashMap<Lexicons, String[]> {

		private static final long serialVersionUID = 1L;
	}
}
