/* Copyright 2013 Udi Cohen
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 this file except in compliance with the License. You may obtain a copy of the
 License at http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */

package org.creativecommons.thelist.authentication;

import fragments.LoginFragment;

public interface ServerAuthenticate {
    public void userSignUp(final String email, final String pass, String authType, LoginFragment.AuthListener listener) throws Exception;
    //TODO: include signupclicklistener as parameter
    public void userSignIn(final String email, final String pass, String authType, String accountType, LoginFragment.AuthListener listener) throws Exception;
}
