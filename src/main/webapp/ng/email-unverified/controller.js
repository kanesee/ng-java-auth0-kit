app.controller('emailUnverifiedCtrl',
  ['$scope'
  ,'$routeParams'
  ,'$http'
  ,'AuthService'
  , controllerFn
  ]);

function controllerFn($scope
                    , $routeParams
                    , $http
                    , AuthService
                    ) {
  
  var PAGE_ID = 'EMAIL_UNVERIFIED'
  
  $scope.isEmailed = false;
  $scope.userid = $routeParams.userid;
  
  // private vars
  var self = this;

  $scope.resendVerifyEmail = function() {
    $scope.isEmailed = false;
    if( $scope.userid ) {
       var url = 'auth0/send_email_verify?userid=' + encodeURIComponent($scope.userid)
              ;
      $http.post(url)
        .then(function(resp) {
          $scope.error = null;
          $scope.isEmailed = true;
        })
        .catch(function(err) {
          console.error(err);
        });
    }
  }
  
  $scope.logIn = function() {
    AuthService.login();
  }
  

}