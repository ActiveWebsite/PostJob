package org.sumdonkus.postJob;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.sumdonkus.postJob.PostJobApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PostJobApplication.class)
@WebAppConfiguration
public class PostJobApplicationTests {

	@Test
	public void contextLoads() {
	}

}
