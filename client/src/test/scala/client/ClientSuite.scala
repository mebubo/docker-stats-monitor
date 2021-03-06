package client

import cats.effect.IO
import fs2.Stream
import munit.FunSuite

import model._

class ClientSuite extends FunSuite {

  implicit private val ioTestDOM = TestDOM[IO]

  implicit private val ioTestCharting = TestCharting[IO]

  private val batch1 = Set(
    ContainerData(
      "974a307336dc",
      "xenodochial_colden",
      "cassandra:3.11.6",
      "3 hours ago",
      "Up 3 hours",
      171.67,
      "1.142GiB / 1.944GiB",
      58.73,
      "766B / 0B",
      "0B / 0B",
      61,
      "884kB (virtual 379MB)",
      "7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp"
    )
  )

  private val batch2 = Set(
    ContainerData(
      "974a307336dc",
      "xenodochial_colden",
      "cassandra:3.11.6",
      "3 hours ago",
      "Up 3 hours",
      4.59,
      "1.147GiB / 1.944GiB",
      59.00,
      "1.05kB / 0B",
      "0B / 0B",
      42,
      "884kB (virtual 379MB)",
      "7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp"
    ),
    ContainerData(
      "a50c5dbb6f4a",
      "happy_stonebraker",
      "cassandra:3.11.6",
      "3 hours ago",
      "Up 3 hours",
      5.51,
      "1.111GiB / 1.944GiB",
      57.14,
      "766B / 0B",
      "0B / 0B",
      61,
      "884kB (virtual 379MB)",
      "7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp"
    )
  )

  private val batch3 = Set(
    ContainerData(
      "a50c5dbb6f4a",
      "happy_stonebraker",
      "cassandra:3.11.6",
      "3 hours ago",
      "Up 3 hours",
      5.58,
      "1.149GiB / 1.944GiB",
      59.08,
      "976B / 0B",
      "0B / 0B",
      43,
      "884kB (virtual 379MB)",
      "7000-7001/tcp, 7199/tcp, 9042/tcp, 9160/tcp"
    )
  )

  private val stream =
    Stream(Set.empty[ContainerData], batch1, batch2, batch3, Set.empty[ContainerData])

  test("client") {
    val states = stream
      .covary[IO]
      .through(new Client[IO].run)
      .compile
      .toList
      .unsafeRunSync()
      .map(_.map.keySet)

    val expected = List(
      Set.empty[String],
      Set("974a307336dc"),
      Set("974a307336dc", "a50c5dbb6f4a"),
      Set("a50c5dbb6f4a"),
      Set.empty[String]
    )

    assertEquals(states, expected)
  }
}
