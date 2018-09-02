package me.texx.Texx

import android.os.Bundle
import com.github.paolorotolo.gitty_reporter.GittyReporter

class BugReportActivity : GittyReporter() {
    override fun init(savedInstanceState: Bundle) {
        setTargetRepository("texxme", "Texx-Android")
        setGuestOAuth2Token("f4f048af0e3f2d36e78b98452d3398fb8c051088")
        enableUserGitHubLogin(false)
        enableGuestGitHubLogin(true)
        setExtraInfo("Example string")
        canEditDebugInfo(false)
    }
}
