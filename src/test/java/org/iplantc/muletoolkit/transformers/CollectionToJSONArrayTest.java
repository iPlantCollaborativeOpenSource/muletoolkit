package org.iplantc.muletoolkit.transformers;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.mule.api.transformer.TransformerException;

/**
 * Unit tests for org.iplantc.muletoolkit.transformers.CollectionToJSONArray.
 * 
 * @author Dennis Roberts
 */
public class CollectionToJSONArrayTest {

    /**
     * The transformer to use in each of the tests.
     */
    private CollectionToJSONArray transformer;

    /**
     * Initializes each of the tests.
     */
    @Before
    public void initialize() {
        transformer = new CollectionToJSONArray();
        transformer.setArrayName("someArray");
    }

    /**
     * Verifies that we can convert a collection to a JSON array.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldConvertCollection() throws Exception {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("foo");
        jsonArray.add("bar");
        jsonArray.add("baz");
        JSONObject expected = new JSONObject();
        expected.put("someArray", jsonArray);
        Collection<String> source = Arrays.asList("foo", "bar", "baz");
        JSONObject actual = JSONObject.fromObject(transformer.transform(source));
        assertEquals(expected, actual);
    }
    
    /**
     * Verifies that we get a TransformerException if we try to convert something that isn't a collection.
     * 
     * @throws Exception if an error occurs.
     */
    @Test(expected = TransformerException.class)
    public void shouldDetectNonCollection() throws Exception {
        transformer.transform("testing");
    }
}
