package app;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import app.model.TestDTO;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class SecondBatchConfig {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public ItemReader<TestDTO> reader(){
		  FlatFileItemReader<TestDTO> reader = new FlatFileItemReader<TestDTO>();
	        reader.setResource(new ClassPathResource("one.csv"));
	        reader.setLineMapper(new DefaultLineMapper<TestDTO>() {{
	            setLineTokenizer(new DelimitedLineTokenizer() {{
	                setNames(new String[] {"id", "name", "city" });
	            }});
	            setFieldSetMapper(new BeanWrapperFieldSetMapper<TestDTO>() {{
	                setTargetType(TestDTO.class);
	            }});
	        }});
	        return reader;
	}
	
	
	 @Bean
	    public ItemWriter<TestDTO> writer() {
	    	FlatFileItemWriter<TestDTO> writer = new FlatFileItemWriter<TestDTO>();
	    	writer.setResource(new ClassPathResource("res.csv"));
	    	DelimitedLineAggregator<TestDTO> delLineAgg = new DelimitedLineAggregator<TestDTO>();
	    	delLineAgg.setDelimiter(",");
	    	BeanWrapperFieldExtractor<TestDTO> fieldExtractor = new BeanWrapperFieldExtractor<TestDTO>();
	    	fieldExtractor.setNames(new String[] {"id", "name","city"});
	    	delLineAgg.setFieldExtractor(fieldExtractor);
	    	writer.setLineAggregator(delLineAgg);
	        return writer;
	    }
	 
	 
	 @Bean
	    public ItemProcessor<TestDTO, TestDTO> processor() {
		 return new ItemProcessor<TestDTO, TestDTO>() {
		        @Override
		        public TestDTO process(TestDTO item) throws Exception {
		        	TestDTO t = new TestDTO();
		        	t.setId(item.getId());
		        	  t.setName(item.getName().toUpperCase());
		        	  t.setCity(item.getCity().toUpperCase());
		            return t;
		        }
		    };
	    }
	 
	 @Bean
	    public Step step() {
	        return stepBuilderFactory.get("step")
	                .<TestDTO, TestDTO> chunk(1)
	                .reader(reader())
	                .processor(processor())
	                .writer(writer())
	                .build();
	    }
	 
	 @Bean
	    public Job createMarkSheet(JobBuilderFactory jobs, Step step) {
	        return jobs.get("createMarkSheet")
	                .flow(step)
	                .end()
	                .build();
	    }

}
