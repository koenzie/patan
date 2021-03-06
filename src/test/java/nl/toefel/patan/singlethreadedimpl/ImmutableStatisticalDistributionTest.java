package nl.toefel.patan.singlethreadedimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.toefel.patan.api.StatisticalDistribution;
import org.junit.Test;

import static nl.toefel.patan.singlethreadedimpl.TimingHelper.assertClose;
import static org.assertj.core.api.Assertions.assertThat;


public class ImmutableStatisticalDistributionTest {

	@Test
	public void testEqualsHashcodeSameName() {
		StatisticalDistribution stat1 = ImmutableStatisticalDistribution.createWithSingleSample(1);
		StatisticalDistribution stat2 = ImmutableStatisticalDistribution.createWithSingleSample(4);
		assertThat(stat1).isNotEqualTo(stat2);
		assertThat(stat1.hashCode()).isNotEqualTo(stat2);
	}

	@Test
	public void testgetEmptyName() {
		assertThat(ImmutableStatisticalDistribution.createEmpty()).isNotNull();
	}

	@Test
	public void testEqualsNull() {
		StatisticalDistribution stat = ImmutableStatisticalDistribution.createWithSingleSample(1);
		assertThat(stat.equals(null)).isFalse();
	}

	@Test
	public void testEqualsHashcodeOtherName() {
		StatisticalDistribution stat1 = ImmutableStatisticalDistribution.createWithSingleSample(1);
		StatisticalDistribution stat2 = ImmutableStatisticalDistribution.createWithSingleSample(4);
		assertThat(stat1).isNotEqualTo(stat2);
		assertThat(stat1.hashCode()).isNotEqualTo(stat2);
	}

	@Test
	public void testToString() {
		StatisticalDistribution a = ImmutableStatisticalDistribution.createWithSingleSample(1);
		assertThat(a.toString())
				.isNotNull()
				.contains(s(a.getSampleCount()),
						s(a.getMinimum()),
						s(a.getMaximum()),
						s(a.getMean()),
						s(a.getStdDeviation()));
	}

	public String s(Number nr) {
		return String.valueOf(nr);
	}

	@Test
	public void testStatistics() {
		testStatistics(createTestDistribution());
		testStatistics(createTestDistributionInverse());
	}

	private void testStatistics(ImmutableStatisticalDistribution dist) {
		assertClose("min", 1d, dist.getMinimum());
		assertClose("man", 10d, dist.getMaximum());
		assertClose("mean", 5.5d, dist.getMean());
        final double expStdDev = Math.sqrt((2 * 4.5d * 4.5d + 2 * 3.5d * 3.5d + 2 * 2.5d * 2.5d + 2 * 1.5d * 1.5d + 2 * 0.5d * 0.5d) / 9);
        assertClose("stdDeviation", expStdDev, dist.getStdDeviation());
	}

	private ImmutableStatisticalDistribution createTestDistribution() {
		ImmutableStatisticalDistribution dist = (ImmutableStatisticalDistribution) ImmutableStatisticalDistribution.createEmpty();
		for (int i = 1; i <= 10; i++) {
			dist = (ImmutableStatisticalDistribution) dist.newWithExtraSample(i);
		}
		return dist;
	}

	private ImmutableStatisticalDistribution createTestDistributionInverse() {
		ImmutableStatisticalDistribution dist = (ImmutableStatisticalDistribution) ImmutableStatisticalDistribution.createEmpty();
		for (int i = 10; i >= 1; i--) {
			dist = (ImmutableStatisticalDistribution) dist.newWithExtraSample(i);
		}
		return dist;
	}

	/** JSON output should look nice. */
	@Test
	public void testToJson() throws JsonProcessingException {
		ImmutableStatisticalDistribution dist = createTestDistribution();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(dist);
		assertThat(json).isEqualTo("{\"sampleCount\":10,\"minimum\":1.0,\"maximum\":10.0,\"mean\":5.5,\"stdDeviation\":3.0276503540974917}");
	}
}