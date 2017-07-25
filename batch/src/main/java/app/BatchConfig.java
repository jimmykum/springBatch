package app;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import app.batch.item.JItemReader;

/*@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration*/
public class BatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	

	@Bean
	public ItemWriter<String> fileWriter() throws Exception {
		FlatFileItemWriter<String> csvFileWriter = new FlatFileItemWriter<>();
		String exportFilePath = "d://temp/out//fch" + System.currentTimeMillis() + ".csv";
		csvFileWriter.setResource(new FileSystemResource(exportFilePath));
		csvFileWriter.setLineAggregator(new PassThroughLineAggregator());
		return csvFileWriter;
	}

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("job").incrementer(new RunIdIncrementer()).start(step1()).build();
	}

	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1").<String, String>chunk(1).reader(new JItemReader("C:\\Users\\User\\Documents\\Notes")).writer(fileWriter())
				.build();
	}
}
