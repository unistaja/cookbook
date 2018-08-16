// Karma configuration
// Generated on Mon Jun 12 2017 15:56:58 GMT+0300 (FLE suveaeg)
//var webpackConfig = require('C:/Users/Kaarel/cookbook/frontend/build/webpack.base.conf.js');
var path = require('path')
var merge = require('webpack-merge')
var baseConfig = require('./build/webpack.base.conf')
var utils = require('./build/utils')
var webpack = require('webpack')
var projectRoot = path.resolve(__dirname, '../../')

var webpackConfig = merge(baseConfig, {
  // use inline sourcemap for karma-sourcemap-loader
  module: {
    loaders: utils.styleLoaders()
  },
  devtool: '#inline-source-map',
  vue: {
    loaders: {
      js: 'babel-loader'
    }
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': require('./config/test.env')
    })
  ]
})

// no need for app entry during tests
delete webpackConfig.entry

// Use babel for test files too
webpackConfig.module.loaders.some(function (loader, i) {
  if (/^babel(-loader)?$/.test(loader.loader)) {
    loader.include.push(path.resolve(projectRoot, 'test/unit'))
    return true
  }
})
module.exports = function(config) {
  config.set({
    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',

    plugins:[
      'karma-webpack',
      'jasmine-core',
      'karma-jasmine',
      'webpack'
    ],

    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['jasmine'],


    // list of files / patterns to load in the browser
    files: [
      './test/*/*Spec.js'
    ],


    // list of files to exclude
    exclude: [
    ],


    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
        './test/unit/test1Spec.js': ['webpack']
    },
    webpack: webpackConfig,
    webpackMiddleware: {
    // webpack-dev-middleware configuration
    // i. e.
    stats: 'errors-only'
  },

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: [],


    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false,

    // Concurrency level
    // how many browser should be started simultaneous
    concurrency: Infinity




  })
}
