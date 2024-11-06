import org.scalatest.flatspec.AnyFlatSpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HelperTest extends AnyFlatSpec {

  val helper: Helper = new Helper {}


  "The calculateAge method" should "return the correct age for a birthdate in the past" in {
    val dob = "1980-01-01"
    val expectedAge = LocalDate.now().getYear - 1980
    val actualAge = helper.calculateAge(dob)
    assert(actualAge === expectedAge)
  }

  it should "return 0 for a birthdate today" in {
    val dob = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val actualAge = helper.calculateAge(dob)
    assert(actualAge === 0)
  }
  
  it should "handle future birth dates correctly (age is 0)" in {
    val futureDob = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val actualAge = helper.calculateAge(futureDob)
    assert(actualAge === 0)
  }

}
