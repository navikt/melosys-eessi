package no.nav.melosys.eessi.service.sed.helpers;

import no.nav.melosys.eessi.models.exception.NotFoundException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostnummerCheckerTest {

    @Test
    public void getPoststed_expectValidPoststed() throws NotFoundException {
        assertThat(PostnummerChecker.getPoststed("0001"), is("OSLO"));
        assertThat(PostnummerChecker.getPoststed("1337"), is("SANDVIKA"));
    }

    @Test(expected = NotFoundException.class)
    public void getPoststed_expectNotFoundException() throws NotFoundException {
        PostnummerChecker.getPoststed("0000");
    }
}