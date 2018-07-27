var app = angular.module('app',
            ['ngRoute'
            ,'ui.bootstrap'
            ,'auth0.auth0'
            ]);

app.config(['$locationProvider', function($locationProvider) {
  $locationProvider.hashPrefix('');
}]);

app.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider
      .when('/', {
        templateUrl: 'ng/home/template.html'
      })
      .when('/auth-required', {
        templateUrl: 'ng/auth-required/template.html'
      })
      .when('/email-unverified/:userid?', {
        templateUrl: 'ng/email-unverified/template.html'
      })
      .otherwise({
        redirectTo: '/'
      });
  }
]);

app.directive('repeatDone', function() {
  return function(scope, element, attrs) {
    if (scope.$last) { // all are rendered
      scope.$eval(attrs.repeatDone);
    }
  }
});

app.filter("emptyToEnd", function () {
    return function (array, key) {
        if(!angular.isArray(array)) return;
        var present = array.filter(function (item) {
            return item[key];
        });
        var empty = array.filter(function (item) {
            return !item[key]
        });
        return present.concat(empty);
    };
});
