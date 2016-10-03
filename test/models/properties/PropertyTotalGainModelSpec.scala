/**
  * Copyright 2016 HM Revenue & Customs
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIED OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package models.properties

import models.resident.shares.TotalGainModel
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.mvc.QueryStringBindable

class PropertyTotalGainModelSpec extends UnitSpec with MockitoSugar {

  def setupMockDoubleBinder(bindValue: Option[Either[String, Double]],
                            unbindValue: String)
                            : QueryStringBindable[Double] = {

    val mockBinder = mock[QueryStringBindable[Double]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

  def setupMockTotalGainBinder(bindValue: Option[Either[String, TotalGainModel]],
                               unbindValue: String)
                              : QueryStringBindable[TotalGainModel] = {

    val mockBinder = mock[QueryStringBindable[TotalGainModel]]

    when(mockBinder.bind(Matchers.any(), Matchers.any()))
      .thenReturn(bindValue)

    when(mockBinder.unbind(Matchers.any(), Matchers.any()))
      .thenReturn(unbindValue)

    mockBinder
  }

}
