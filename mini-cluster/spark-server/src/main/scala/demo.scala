//import org.apache.spark.sql.catalyst.InternalRow
//def generate(references: Array[AnyRef]) =
//  new Dummy#GeneratedIteratorForCodegenStage1(references)
//
///*wsc_codegenStageId*/
//final class GeneratedIteratorForCodegenStage1(var references: Array[AnyRef])
//    extends BufferedRowIterator {
//  private var inputs = null
//  private val scan_mutableStateArray_0 =
//    new Array[Iterator[_]](1)
//  override def init(index: Int, inputs: Array[Iterator[_]]): Unit = {
//    partitionIndex = index
//    this.inputs = inputs
//    scan_mutableStateArray_0(0) = inputs(0)
//  }
//  @throws[java.io.IOException]
//  override protected def processNext(): Unit = {
//    while ({ scan_mutableStateArray_0(0).hasNext }) {
//      val scan_row_0 = scan_mutableStateArray_0(0).next
//        .asInstanceOf[InternalRow]
//      references(0).asInstanceOf[SQLMetric] /* numOutputRows */.add(1)
//      append(scan_row_0)
//      if (shouldStop) return
//    }
//  }
//}
