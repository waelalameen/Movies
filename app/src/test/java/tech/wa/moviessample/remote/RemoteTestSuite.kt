package tech.wa.moviessample.remote

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    SearchTest::class,
    DetailsTest::class
)
class RemoteTestSuite