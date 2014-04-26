/**
 * @see http://algs4.cs.princeton.edu/44sp/tinyEWD.txt
 */
package org.gs.graph

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import scala.util.control.Breaks._

/**
 * @author Gary Struthers
 *
 */
@RunWith(classOf[JUnitRunner])
class DijkstraSPSuite extends FlatSpec {
  trait Builder {
    val tinyEWDData = Array((4, 5, 0.35), (5, 4, 0.35), (4, 7, 0.37), (5, 7, 0.28), (7, 5, 0.28),
      (5, 1, 0.32), (0, 4, 0.38), (0, 2, 0.26), (7, 3, 0.39), (1, 3, 0.29), (2, 7, 0.34),
      (6, 2, 0.40), (3, 6, 0.52), (6, 0, 0.58), (6, 4, 0.93))
    val size = tinyEWDData.size
    val tinyEdgeArray = {
      for {
        e <- tinyEWDData
      } yield new DirectedEdge(e._1, e._2, e._3)
    }
  }

  trait GraphBuilder extends Builder {
    val g = new EdgeWeightedDigraph(size)
    for {
      ed <- tinyEdgeArray
    } {
      g.addEdge(ed)
    }
  }
  trait DijkstraSPBuilder extends GraphBuilder {

    val s0 = 0
    val dsp0 = new DijkstraSP(g, s0)
  }

  behavior of "a EdgeWeightedDigraph"

  it should "have no negative weights" in new GraphBuilder {
    assert(g.edges.forall(_.weight >= 0))
  }

  behavior of "a DijkstraSP"
  it should "build" in new GraphBuilder {
    val dsp = new DijkstraSP(g, 0)
    assert(dsp !== null)
  }

  it should "have consistent distTo and edgeTo for the source vertex" in new DijkstraSPBuilder {
    val distTo = dsp0.distTo(s0)
    val edgeTo = dsp0.edgeTo(s0)
    val consistent = if (distTo != 0.0 || edgeTo != null) false else true
    assert(consistent)
  }

  it should "have consistent distTo and edgeTo for all verticies" in new DijkstraSPBuilder {
    var consistent = true
    breakable {
      for {
        v <- 0 until g.v
        if (v != s0)
      } {
        if (dsp0.edgeTo(v) == null && dsp0.distTo(v) != Double.PositiveInfinity) {
          consistent = false
          break
        }
      }
    }
    assert(consistent)
  }

  it should "have all edges where distTo(w) <= distTo(v) + e.weight" in new DijkstraSPBuilder {
    var valid = true
    breakable {
      for {
        v <- 0 until g.v
        e <- g.adj(v)
      } {
        val w = e.to
        if (dsp0.distTo(v) + e.weight < dsp0.distTo(w)) {
          valid = false
          break
        }
      }
    }
    assert(valid)
  }

  it should "have all edges where distTo(w) == distTo(v) + e.weight" in new DijkstraSPBuilder {
    var valid = true
    breakable {
      for {
        w <- 0 until g.v
      } {
        val e = dsp0.edgeTo(w)
        if (e != null) {
          val v = e.from
          if (w != e.to) {
            valid = false
            break
          }
          if (dsp0.distTo(v) + e.weight != dsp0.distTo(w)) {
            valid = false
            break
          }
        }
      }
    }
    assert(valid)
  }

  it should "have shortest paths from 0 to all vertices" in new DijkstraSPBuilder {
    val equals = (_: DirectedEdge) == (_: DirectedEdge)
    for {
      v <- 0 until g.v
    } {
      v match {
        case 0 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List[DirectedEdge]())(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case 1 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(6), tinyEdgeArray(0), tinyEdgeArray(5)))(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case 2 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(7)))(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case 3 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(7), tinyEdgeArray(10), tinyEdgeArray(8)))(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case 4 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(6)))(equals))
          case None =>
        }
        case 5 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(6), tinyEdgeArray(0)))(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case 6 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(7), tinyEdgeArray(10), tinyEdgeArray(8), tinyEdgeArray(12)))(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case 7 => dsp0.pathTo(v) match {
          case Some(x) => assert(x.corresponds(List(tinyEdgeArray(7), tinyEdgeArray(10)))(equals))
          case None => fail(s"path from 0 to $v not found")
        }
        case x if 8 until g.v contains x => assertResult(None) {dsp0.pathTo(v)}
        case _ => fail(s"v:$v is not a valid vertex")
      }
    }

  }
}